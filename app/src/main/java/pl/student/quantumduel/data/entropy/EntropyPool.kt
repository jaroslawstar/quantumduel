package pl.student.quantumduel.data.entropy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyStatus

/**
 * Pula bajtów z kwantowego generatora liczb losowych. ETAP WSTĘPNY.
 *
 * Plan działania:
 * - bajty pobierane partiami ([Config.batchSize] na zapytanie), bo darmowe API ma limit zapytań,
 * - gdy w puli zostanie mniej niż [Config.refillThreshold] bajtów, w tle pobierana jest kolejna partia,
 * - każdy bajt wydawany tylko raz, w kolejności pobrania,
 * - dostawcy próbowani po kolei (np. ANU, potem Outshift), każdy z limitem czasu [Config.requestTimeoutMillis],
 * - gdy wszyscy zawiodą: bajty z [LocalEntropySource] i karencja [Config.failureCooldownMillis] bez odpytywania sieci,
 * - stan puli publikowany w [status].
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

    private val _status = MutableStateFlow(EntropyStatus())
    val status: StateFlow<EntropyStatus> = _status.asStateFlow()

    // TODO: bufor ArrayDeque<EntropyByte> chroniony przez Mutex, zadanie dobierania (Job), czas końca karencji

    /** Kolejny bajt: z puli, a gdy jest pusta – po pobraniu partii albo z SecureRandom. */
    suspend fun nextByte(): EntropyByte {
        // TODO: wziąć bajt z bufora; pusty bufor → poczekać na pobranie partii; brak danych → localByte()
        TODO("EntropyPool.nextByte")
    }

    /** Bajt z lokalnego generatora, z pominięciem puli. */
    fun localByte(): EntropyByte {
        // TODO: EntropyByte(local.nextByte(), EntropyOrigin.LOCAL) i aktualizacja status.lastOrigin
        TODO("EntropyPool.localByte")
    }

    /** Uruchamia pobieranie w tle. [force] pomija karencję i próg zapełnienia puli. */
    fun prefetch(force: Boolean = false) {
        // TODO: scope.launch { … } – pobrać partię od pierwszego działającego dostawcy
        TODO("EntropyPool.prefetch")
    }

    /** Liczba bajtów w puli (do testów). */
    suspend fun size(): Int = TODO("EntropyPool.size")
}
