package pl.student.quantumduel.data.entropy

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.entropy.EntropyStatus

/**
 * Pula bajtów z kwantowego generatora liczb losowych.
 *
 * - Bajty są pobierane partiami (domyślnie 1024 na zapytanie), bo darmowe API ma limit zapytań.
 * - Gdy w puli zostanie mniej niż [Config.refillThreshold] bajtów, w tle pobierana jest kolejna partia.
 * - Każdy bajt jest wydawany tylko raz.
 * - Dostawcy są próbowani po kolei (np. ANU, potem Outshift). Gdy wszyscy zawiodą, pula przechodzi
 *   na [LocalEntropySource] i przez [Config.failureCooldownMillis] nie odpytuje sieci, żeby kolejne
 *   kliknięcia nie czekały na timeout.
 */
class EntropyPool(
    private val scope: CoroutineScope,
    private val providers: suspend () -> List<QuantumEntropyProvider>,
    private val local: LocalEntropySource,
    private val config: Config = Config(),
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
) {

    data class Config(
        val batchSize: Int = 1024,
        val refillThreshold: Int = 256,
        val maxBuffered: Int = 4096,
        val requestTimeoutMillis: Long = 5_000,
        val failureCooldownMillis: Long = 60_000,
        val noProviderRecheckMillis: Long = 3_000,
    )

    private val mutex = Mutex()
    private val buffer = ArrayDeque<EntropyByte>()
    private var refillJob: Job? = null
    private var cooldownUntil = 0L

    private val _status = MutableStateFlow(EntropyStatus())
    val status: StateFlow<EntropyStatus> = _status.asStateFlow()

    /** Kolejny bajt: z puli, a gdy jest pusta – po pobraniu partii albo z SecureRandom. */
    suspend fun nextByte(): EntropyByte {
        takeBuffered()?.let { return it }

        val job = mutex.withLock { startRefillLocked(force = false) }
        job?.join()

        takeBuffered()?.let { return it }
        return localByte()
    }

    /** Bajt z lokalnego generatora, z pominięciem puli. */
    fun localByte(): EntropyByte {
        val byte = EntropyByte(local.nextByte(), EntropyOrigin.LOCAL)
        _status.update { it.copy(lastOrigin = EntropyOrigin.LOCAL) }
        return byte
    }

    /** Uruchamia pobieranie w tle. [force] pomija karencję i próg zapełnienia puli. */
    fun prefetch(force: Boolean = false) {
        scope.launch {
            mutex.withLock {
                if (force) cooldownUntil = 0L
                startRefillLocked(force)
            }
        }
    }

    /** Liczba bajtów w puli (do testów). */
    suspend fun size(): Int = mutex.withLock { buffer.size }

    private suspend fun takeBuffered(): EntropyByte? = mutex.withLock {
        val byte = buffer.removeFirstOrNull() ?: return@withLock null
        _status.update { it.copy(pooledBytes = buffer.size, lastOrigin = byte.origin) }
        if (buffer.size < config.refillThreshold) startRefillLocked(force = false)
        byte
    }

    /** Wywoływane pod [mutex]. Zwraca trwające lub nowe zadanie pobierania albo null, gdy nie trzeba pobierać. */
    private fun startRefillLocked(force: Boolean): Job? {
        refillJob?.let { if (it.isActive) return it }
        if (!force && nowMillis() < cooldownUntil) return null
        if (!force && buffer.size >= config.refillThreshold) return null
        val job = scope.launch { refill() }
        refillJob = job
        return job
    }

    private suspend fun refill() {
        val chain = providers()
        if (chain.isEmpty()) {
            mutex.withLock { cooldownUntil = nowMillis() + config.noProviderRecheckMillis }
            _status.update { it.copy(isFetching = false, quantumConfigured = false, lastError = null) }
            return
        }

        _status.update { it.copy(isFetching = true, quantumConfigured = true) }
        val errors = mutableListOf<String>()
        try {
            for (provider in chain) {
                val bytes = fetchFrom(provider, errors)
                if (!bytes.isNullOrEmpty()) {
                    mutex.withLock {
                        bytes.forEach { buffer.addLast(EntropyByte(it and 0xFF, provider.origin)) }
                        while (buffer.size > config.maxBuffered) buffer.removeFirst()
                        _status.update {
                            it.copy(pooledBytes = buffer.size, lastError = errors.joinToString("; ").ifEmpty { null })
                        }
                    }
                    return
                }
            }
            mutex.withLock { cooldownUntil = nowMillis() + config.failureCooldownMillis }
            _status.update { it.copy(lastError = errors.joinToString("; ")) }
        } finally {
            _status.update { it.copy(isFetching = false) }
        }
    }

    private suspend fun fetchFrom(provider: QuantumEntropyProvider, errors: MutableList<String>): List<Int>? =
        try {
            val result = withTimeoutOrNull(config.requestTimeoutMillis) { provider.fetch(config.batchSize) }
            if (result == null) errors += "${provider.origin}: przekroczono czas odpowiedzi"
            if (result != null && result.isEmpty()) errors += "${provider.origin}: pusta odpowiedź"
            result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errors += "${provider.origin}: ${e.message ?: e::class.simpleName}"
            null
        }
}
