package pl.student.quantumduel.domain.settings

import kotlinx.coroutines.flow.Flow
import pl.student.quantumduel.domain.game.GameMode

/** Preferowane źródło losowości. Przy błędzie sieci aplikacja i tak spada na SecureRandom. */
enum class RandomnessSource {
    /** ANU Quantum Numbers, zapasowo Outshift. */
    ANU,

    /** Outshift QRNG (Cisco), zapasowo ANU. */
    OUTSHIFT,

    /** Tylko lokalny SecureRandom – bez internetu. */
    LOCAL,
}

data class AppSettings(
    val playerAName: String = DEFAULT_PLAYER_A,
    val playerBName: String = DEFAULT_PLAYER_B,
    val defaultMode: GameMode = GameMode.SLOTS,
    val randomnessSource: RandomnessSource = RandomnessSource.ANU,
    val anuApiKey: String = "",
    val outshiftApiKey: String = "",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
) {
    /** Czy jest skonfigurowane jakiekolwiek źródło kwantowe. */
    val quantumConfigured: Boolean
        get() = randomnessSource != RandomnessSource.LOCAL &&
            (anuApiKey.isNotBlank() || outshiftApiKey.isNotBlank())

    companion object {
        const val DEFAULT_PLAYER_A = "Gracz A"
        const val DEFAULT_PLAYER_B = "Gracz B"
        const val MAX_NAME_LENGTH = 16
    }
}

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun update(transform: (AppSettings) -> AppSettings)
}
