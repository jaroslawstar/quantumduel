package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.history.HistoryRepository
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.RandomnessSource
import pl.student.quantumduel.domain.settings.SettingsRepository
import pl.student.quantumduel.domain.usecase.PlayRoundUseCase
import javax.inject.Inject

/** Etapy losowania widoczne w interfejsie. */
enum class DrawPhase {
    /** Przed pierwszym losowaniem lub po zmianie trybu. */
    IDLE,

    /** Bębny się kręcą, trwa pomiar kubitów. */
    SPINNING,

    /** Wynik znany, bębny zatrzymują się jeden po drugim. */
    REVEALING,

    /** Zwycięzca podświetlony. */
    REVEALED,
}

data class GameUiState(
    val mode: GameMode = GameMode.SLOTS,
    val phase: DrawPhase = DrawPhase.IDLE,
    /** Ile bębnów (pozycji w okienku) już się zatrzymało w fazie REVEALING. */
    val revealedSlots: Int = 0,
    val round: DuelRound? = null,
    val playerAName: String = AppSettings.DEFAULT_PLAYER_A,
    val playerBName: String = AppSettings.DEFAULT_PLAYER_B,
    val sessionWinsA: Int = 0,
    val sessionWinsB: Int = 0,
    val entropy: EntropyStatus = EntropyStatus(),
    val randomnessSource: RandomnessSource = RandomnessSource.ANU,
    val quantumConfigured: Boolean = false,
    val errorMessage: String? = null,
) {
    val isBusy: Boolean get() = phase == DrawPhase.SPINNING || phase == DrawPhase.REVEALING

    /** Zwycięzca jest pokazywany dopiero po zatrzymaniu wszystkich bębnów. */
    val visibleWinner: Player? get() = if (phase == DrawPhase.REVEALED) round?.winner else null

    /** Brak klucza API przy wybranym źródle kwantowym – pokażemy podpowiedź. */
    val showMissingKeyHint: Boolean
        get() = randomnessSource != RandomnessSource.LOCAL && !quantumConfigured
}

/** Jednorazowe efekty (dźwięk, wibracja) – nie są częścią stanu, żeby nie powtarzały się po obrocie ekranu. */
sealed interface GameEffect {
    data class RoundFinished(val winner: Player, val playSound: Boolean, val vibrate: Boolean) : GameEffect
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val playRound: PlayRoundUseCase,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository,
    private val entropyRepository: EntropyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _effects = Channel<GameEffect>(Channel.BUFFERED)
    val effects: Flow<GameEffect> = _effects.receiveAsFlow()

    private var settings = AppSettings()
    private var defaultModeApplied = false

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { s ->
                settings = s
                _uiState.update { state ->
                    state.copy(
                        playerAName = s.playerAName.ifBlank { AppSettings.DEFAULT_PLAYER_A },
                        playerBName = s.playerBName.ifBlank { AppSettings.DEFAULT_PLAYER_B },
                        randomnessSource = s.randomnessSource,
                        quantumConfigured = s.quantumConfigured,
                        // Tryb domyślny ustawiamy tylko raz – późniejsza zmiana ustawień nie przerywa gry.
                        mode = if (defaultModeApplied || state.isBusy) state.mode else s.defaultMode,
                    )
                }
                defaultModeApplied = true
            }
        }
        viewModelScope.launch {
            entropyRepository.status.collect { status -> _uiState.update { it.copy(entropy = status) } }
        }
        // Pierwsza partia bajtów pobiera się w tle, więc pierwsze kliknięcie nie czeka na sieć.
        entropyRepository.prefetch()
    }

    fun onModeSelected(mode: GameMode) {
        if (_uiState.value.isBusy || _uiState.value.mode == mode) return
        _uiState.update { it.copy(mode = mode, phase = DrawPhase.IDLE, round = null, revealedSlots = 0, errorMessage = null) }
    }

    fun onDrawClicked() {
        val start = _uiState.value
        if (start.isBusy) return
        _uiState.update { it.copy(phase = DrawPhase.SPINNING, round = null, revealedSlots = 0, errorMessage = null) }

        viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            val round = try {
                playRound(start.mode)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(phase = DrawPhase.IDLE, errorMessage = e.message ?: "Nieznany błąd losowania") }
                return@launch
            }

            // Animacja kręcenia trwa co najmniej chwilę, nawet gdy bajty były już w puli.
            val elapsed = System.currentTimeMillis() - startedAt
            if (elapsed < MIN_SPIN_MILLIS) delay(MIN_SPIN_MILLIS - elapsed)

            _uiState.update { it.copy(phase = DrawPhase.REVEALING, round = round, revealedSlots = 0) }
            val positions = if (round.mode == GameMode.SLOTS) PlayRoundUseCase.REELS else 1
            for (i in 1..positions) {
                delay(REEL_STOP_DELAY_MILLIS)
                _uiState.update { it.copy(revealedSlots = i) }
            }

            _uiState.update {
                it.copy(
                    phase = DrawPhase.REVEALED,
                    sessionWinsA = it.sessionWinsA + if (round.winner == Player.A) 1 else 0,
                    sessionWinsB = it.sessionWinsB + if (round.winner == Player.B) 1 else 0,
                )
            }
            _effects.send(GameEffect.RoundFinished(round.winner, settings.soundEnabled, settings.vibrationEnabled))

            try {
                historyRepository.save(round)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Nie zapisano rundy w historii: ${e.message}") }
            }
        }
    }

    fun onResetScore() {
        if (_uiState.value.isBusy) return
        _uiState.update { it.copy(sessionWinsA = 0, sessionWinsB = 0) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        const val MIN_SPIN_MILLIS = 900L
        const val REEL_STOP_DELAY_MILLIS = 280L
    }
}
