package pl.student.quantumduel.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import pl.student.quantumduel.data.entropy.AnuQrngProvider
import pl.student.quantumduel.data.entropy.EntropyPool
import pl.student.quantumduel.data.entropy.LocalEntropySource
import pl.student.quantumduel.data.entropy.OutshiftQrngProvider
import pl.student.quantumduel.data.entropy.QuantumEntropyProvider
import pl.student.quantumduel.di.ApplicationScope
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Łączy ustawienia użytkownika z pulą entropii: wybiera kolejność dostawców QRNG. ETAP WSTĘPNY. */
@Singleton
class EntropyRepositoryImpl @Inject constructor(
    private val anu: AnuQrngProvider,
    private val outshift: OutshiftQrngProvider,
    private val settingsRepository: SettingsRepository,
    @ApplicationScope scope: CoroutineScope,
) : EntropyRepository {

    private val pool = EntropyPool(
        scope = scope,
        providers = { providerChain() },
        local = LocalEntropySource(),
    )

    override val status: StateFlow<EntropyStatus> = pool.status

    override suspend fun nextByte(): EntropyByte {
        // TODO: przy źródle LOCAL w ustawieniach → pool.localByte(), w przeciwnym razie pool.nextByte()
        TODO("EntropyRepositoryImpl.nextByte")
    }

    override fun prefetch(force: Boolean) {
        // TODO: pool.prefetch(force)
        TODO("EntropyRepositoryImpl.prefetch")
    }

    private suspend fun providerChain(): List<QuantumEntropyProvider> {
        // TODO: ANU → [ANU, Outshift], OUTSHIFT → [Outshift, ANU], LOCAL → pusta lista; pominąć dostawców bez klucza
        TODO("EntropyRepositoryImpl.providerChain")
    }
}
