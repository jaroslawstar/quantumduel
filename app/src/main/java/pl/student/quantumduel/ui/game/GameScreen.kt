package pl.student.quantumduel.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.presentation.GameUiState
import pl.student.quantumduel.presentation.GameViewModel
import pl.student.quantumduel.ui.components.ModeSelector

const val DRAW_BUTTON_TAG = "draw_button"

/** Punkt wejścia ekranu: łączy ViewModel z bezstanowym [GameScreen]. */
@Composable
fun GameRoute(
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO: zbieranie viewModel.effects – wibracja (LocalHapticFeedback) i dźwięk (ToneGenerator) po losowaniu

    GameScreen(
        state = state,
        onDrawClick = viewModel::onDrawClicked,
        onModeSelected = viewModel::onModeSelected,
        onOpenHistory = onOpenHistory,
        onOpenSettings = onOpenSettings,
        onResetScore = viewModel::onResetScore,
        onErrorShown = viewModel::onErrorShown,
    )
}

/**
 * Ekran pojedynku. ETAP WSTĘPNY: docelowy układ (okienko A, przycisk LOSUJ z wynikiem, okienko B)
 * bez animacji i wyników.
 *
 * TODO: układ poziomy (A | przycisk | B) dla orientacji landscape
 * TODO: podpowiedź o braku klucza API, informacja o źródle losowości i liczbie pomiarów
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameUiState,
    onDrawClick: () -> Unit,
    onModeSelected: (GameMode) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onResetScore: () -> Unit = {},
    onErrorShown: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onErrorShown()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.history_title))
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ModeSelector(
                selected = state.mode,
                onSelect = onModeSelected,
                enabled = !state.isBusy,
                modifier = Modifier.fillMaxWidth(),
            )
            DuelPanel(
                name = state.playerAName,
                mode = state.mode,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.session_score, state.sessionWinsA, state.sessionWinsB),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    TextButton(onClick = onResetScore, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                        Text(stringResource(R.string.reset_score), style = MaterialTheme.typography.labelSmall)
                    }
                }
                DrawButton(enabled = !state.isBusy, onClick = onDrawClick)
            }
            DuelPanel(
                name = state.playerBName,
                mode = state.mode,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

/** Okrągły przycisk LOSUJ. TODO: pulsowanie i wskaźnik postępu w czasie losowania. */
@Composable
private fun DrawButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .size(104.dp)
            .testTag(DRAW_BUTTON_TAG),
    ) {
        Text(
            text = stringResource(R.string.draw_button),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
        )
    }
}
