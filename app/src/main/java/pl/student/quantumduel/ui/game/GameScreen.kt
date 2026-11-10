package pl.student.quantumduel.ui.game

import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.presentation.DrawPhase
import pl.student.quantumduel.presentation.GameEffect
import pl.student.quantumduel.presentation.GameUiState
import pl.student.quantumduel.presentation.GameViewModel
import pl.student.quantumduel.ui.components.ModeSelector
import pl.student.quantumduel.ui.components.OriginBadge
import pl.student.quantumduel.ui.components.labelRes

const val DRAW_BUTTON_TAG = "draw_button"

/** Punkt wejścia ekranu: łączy ViewModel z bezstanowym [GameScreen] i obsługuje dźwięk/wibrację. */
@Composable
fun GameRoute(
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val toneGenerator = remember { runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 60) }.getOrNull() }
    DisposableEffect(toneGenerator) {
        onDispose { toneGenerator?.release() }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GameEffect.RoundFinished -> {
                    if (effect.vibrate) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (effect.playSound) toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                }
            }
        }
    }

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
            if (state.showMissingKeyHint) MissingKeyHint(onOpenSettings = onOpenSettings)

            val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (landscape) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlayerPanel(state, Player.A, Modifier.weight(1f).fillMaxHeight())
                    CenterControls(state, onDrawClick, onResetScore, vertical = true, modifier = Modifier.width(150.dp))
                    PlayerPanel(state, Player.B, Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PlayerPanel(state, Player.A, Modifier.weight(1f).fillMaxWidth())
                    CenterControls(state, onDrawClick, onResetScore, vertical = false, modifier = Modifier.fillMaxWidth())
                    PlayerPanel(state, Player.B, Modifier.weight(1f).fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun PlayerPanel(state: GameUiState, player: Player, modifier: Modifier) {
    DuelPanel(
        player = player,
        name = if (player == Player.A) state.playerAName else state.playerBName,
        sessionWins = if (player == Player.A) state.sessionWinsA else state.sessionWinsB,
        mode = state.mode,
        phase = state.phase,
        revealedSlots = state.revealedSlots,
        result = state.round?.panelOf(player),
        winner = state.visibleWinner,
        modifier = modifier,
    )
}

/** Przycisk LOSUJ oraz informacje o rundzie: źródło losowości, liczba pomiarów, wynik sesji. */
@Composable
private fun CenterControls(
    state: GameUiState,
    onDrawClick: () -> Unit,
    onResetScore: () -> Unit,
    vertical: Boolean,
    modifier: Modifier = Modifier,
) {
    if (vertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScoreBoard(state, onResetScore)
            DrawButton(busy = state.isBusy, onClick = onDrawClick)
            RoundInfo(state)
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScoreBoard(state, onResetScore, modifier = Modifier.weight(1f))
            DrawButton(busy = state.isBusy, onClick = onDrawClick)
            RoundInfo(state, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ScoreBoard(state: GameUiState, onResetScore: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.session_score, state.sessionWinsA, state.sessionWinsB),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        TextButton(
            onClick = onResetScore,
            enabled = !state.isBusy && (state.sessionWinsA + state.sessionWinsB) > 0,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        ) {
            Text(stringResource(R.string.reset_score), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun DrawButton(busy: Boolean, onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "drawPulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900), RepeatMode.Reverse),
        label = "drawPulseScale",
    )
    Button(
        onClick = onClick,
        enabled = !busy,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(),
        modifier = Modifier
            .size(104.dp)
            .graphicsLayer {
                val scale = if (busy) 1f else pulse
                scaleX = scale
                scaleY = scale
            }
            .testTag(DRAW_BUTTON_TAG),
    ) {
        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                text = stringResource(R.string.draw_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
private fun RoundInfo(state: GameUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val round = state.round
        when {
            state.phase == DrawPhase.REVEALED && round != null -> {
                OriginBadge(origin = round.origin)
                Text(
                    text = stringResource(R.string.round_measurements, round.attempts, round.measuredQubits),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (round.ties > 0) {
                    Text(
                        text = stringResource(R.string.round_ties, round.ties),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }

            state.isBusy -> Text(
                text = stringResource(R.string.measuring_mode, stringResource(state.mode.labelRes)),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            else -> PoolInfo(state)
        }
    }
}

@Composable
private fun PoolInfo(state: GameUiState) {
    val entropy = state.entropy
    val text = when {
        entropy.isFetching -> stringResource(R.string.pool_fetching)
        entropy.pooledBytes > 0 -> stringResource(R.string.pool_bytes, entropy.pooledBytes)
        else -> stringResource(R.string.pool_local)
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun MissingKeyHint(onOpenSettings: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.missing_key_hint),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onOpenSettings) { Text(stringResource(R.string.open_settings)) }
        }
    }
}
