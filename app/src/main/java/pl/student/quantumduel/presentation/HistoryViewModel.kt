package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.student.quantumduel.domain.history.HistoryRepository
import pl.student.quantumduel.domain.history.RoundRecord
import pl.student.quantumduel.domain.settings.AppSettings
import pl.student.quantumduel.domain.settings.SettingsRepository
import pl.student.quantumduel.domain.usecase.DuelStats
import pl.student.quantumduel.domain.usecase.GetStatsUseCase
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val records: List<RoundRecord> = emptyList(),
    val stats: DuelStats = DuelStats(),
    val playerAName: String = AppSettings.DEFAULT_PLAYER_A,
    val playerBName: String = AppSettings.DEFAULT_PLAYER_B,
)

/** ViewModel historii i statystyk. ETAP WSTĘPNY: na razie stały, pusty stan. */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    settingsRepository: SettingsRepository,
    getStats: GetStatsUseCase,
) : ViewModel() {

    // TODO: combine(historyRepository.observeHistory(), settingsRepository.settings) → HistoryUiState
    //       ze statystykami z getStats(records), wystawione przez stateIn(viewModelScope, WhileSubscribed(5 000))
    val uiState: StateFlow<HistoryUiState> = MutableStateFlow(HistoryUiState(isLoading = false)).asStateFlow()

    fun onClearHistory() {
        // TODO: viewModelScope.launch { historyRepository.clear() }
    }
}
