package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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

/**
 * ViewModel ekranu gry. ETAP WSTĘPNY: stan i zdarzenia są zdefiniowane, a zależności wstrzykiwane przez Hilt,
 * ale losowanie nie jest jeszcze podłączone.
 */
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

    // TODO (init): subskrypcja settingsRepository.settings (nazwy graczy, tryb domyślny, źródło losowości),
    //              subskrypcja entropyRepository.status oraz entropyRepository.prefetch()

    fun onModeSelected(mode: GameMode) {
        if (_uiState.value.isBusy || _uiState.value.mode == mode) return
        _uiState.update { it.copy(mode = mode, phase = DrawPhase.IDLE, round = null, revealedSlots = 0, errorMessage = null) }
    }

    fun onDrawClicked() {
        // TODO: faza SPINNING → playRound(mode) → co najmniej MIN_SPIN_MILLIS animacji →
        //       REVEALING (bęben po bębnie co REEL_STOP_DELAY_MILLIS) → REVEALED → wynik sesji →
        //       efekt RoundFinished → historyRepository.save(round)
        _uiState.update { it.copy(errorMessage = NOT_IMPLEMENTED_MESSAGE) }
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
        private const val NOT_IMPLEMENTED_MESSAGE = "Etap wstępny: losowanie nie jest jeszcze zaimplementowane"
    }
}
