package pl.student.quantumduel.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import pl.student.quantumduel.data.entropy.AnuQrngProvider
import pl.student.quantumduel.data.entropy.EntropyPool
import pl.student.quantumduel.data.entropy.LocalEntropySource
import pl.student.quantumduel.data.entropy.OutshiftQrngProvider
import pl.student.quantumduel.data.entropy.QuantumEntropyProvider
import pl.student.quantumduel.di.ApplicationScope
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.settings.RandomnessSource
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Łączy ustawienia użytkownika z pulą entropii: wybiera kolejność dostawców QRNG. */
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
        val settings = settingsRepository.settings.first()
        return if (settings.randomnessSource == RandomnessSource.LOCAL) pool.localByte() else pool.nextByte()
    }

    override fun prefetch(force: Boolean) = pool.prefetch(force)

    private suspend fun providerChain(): List<QuantumEntropyProvider> {
        val s = settingsRepository.settings.first()
        val anuReady = anu.takeIf { s.anuApiKey.isNotBlank() }
        val outshiftReady = outshift.takeIf { s.outshiftApiKey.isNotBlank() }
        return when (s.randomnessSource) {
            RandomnessSource.ANU -> listOfNotNull(anuReady, outshiftReady)
            RandomnessSource.OUTSHIFT -> listOfNotNull(outshiftReady, anuReady)
            RandomnessSource.LOCAL -> emptyList()
        }
    }
}
