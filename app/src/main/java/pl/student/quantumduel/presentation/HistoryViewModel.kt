package pl.student.quantumduel.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    settingsRepository: SettingsRepository,
    getStats: GetStatsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = combine(
        historyRepository.observeHistory(),
        settingsRepository.settings,
    ) { records, settings ->
        HistoryUiState(
            isLoading = false,
            records = records,
            stats = getStats(records),
            playerAName = settings.playerAName.ifBlank { AppSettings.DEFAULT_PLAYER_A },
            playerBName = settings.playerBName.ifBlank { AppSettings.DEFAULT_PLAYER_B },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun onClearHistory() {
        viewModelScope.launch { historyRepository.clear() }
    }
}
