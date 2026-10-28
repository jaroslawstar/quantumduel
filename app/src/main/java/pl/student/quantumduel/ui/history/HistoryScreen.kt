package pl.student.quantumduel.ui.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.presentation.HistoryUiState
import pl.student.quantumduel.presentation.HistoryViewModel
import pl.student.quantumduel.ui.components.PlannedFeatures

@Composable
fun HistoryRoute(onBack: () -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onBack = onBack, onClearHistory = viewModel::onClearHistory)
}

/**
 * Historia i statystyki. ETAP WSTĘPNY: ekran zastępczy z listą planowanych funkcji.
 *
 * TODO: karta wygranych, rozkłady symboli i ruchów z testem χ², lista rund (LazyColumn),
 *       przycisk czyszczenia historii z oknem potwierdzenia (onClearHistory)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(state: HistoryUiState, onBack: () -> Unit, onClearHistory: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        PlannedFeatures(
            intro = stringResource(R.string.placeholder_history, state.records.size),
            items = stringArrayResource(R.array.plan_history).toList(),
            modifier = Modifier.padding(padding),
        )
    }
}
