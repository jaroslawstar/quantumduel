package pl.student.quantumduel.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.PanelResult
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.RpsPanel
import pl.student.quantumduel.domain.game.SlotSymbol
import pl.student.quantumduel.domain.game.SlotsPanel
import pl.student.quantumduel.presentation.DrawPhase
import pl.student.quantumduel.ui.components.emoji
import pl.student.quantumduel.ui.components.labelRes
import pl.student.quantumduel.ui.theme.KetTextStyle

private const val SPIN_FRAME_MILLIS = 85L
private val SLOT_EMOJI = SlotSymbol.entries.map { it.emoji }
private val RPS_EMOJI = RpsMove.entries.map { it.emoji }

/**
 * Okienko jednego gracza: nagłówek, bębny (lub dłoń w KPN), zmierzone stany kubitów i opis układu.
 * Komponent jest bezstanowy – wszystko, co pokazuje, przychodzi z [GameUiState].
 */
@Composable
fun DuelPanel(
    player: Player,
    name: String,
    sessionWins: Int,
    mode: GameMode,
    phase: DrawPhase,
    revealedSlots: Int,
    result: PanelResult?,
    winner: Player?,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isWinner = winner == player
    val isLoser = winner != null && winner != player

    val borderColor by animateColorAsState(if (isWinner) colors.primary else colors.outlineVariant, label = "panelBorder")
    val borderWidth by animateDpAsState(if (isWinner) 3.dp else 1.dp, label = "panelBorderWidth")
    val panelScale by animateFloatAsState(if (isWinner) 1.02f else 1f, label = "panelScale")
    val panelAlpha by animateFloatAsState(if (isLoser) 0.55f else 1f, label = "panelAlpha")

    Card(
        modifier = modifier.graphicsLayer {
            scaleX = panelScale
            scaleY = panelScale
            alpha = panelAlpha
        },
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) colors.primaryContainer else colors.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PanelHeader(name = name, sessionWins = sessionWins, isWinner = isWinner)

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val positions = if (mode == GameMode.SLOTS) 3 else 1
                val gap = 10.dp
                val byWidth = (maxWidth - gap * (positions - 1)) / positions
                val reelSize: Dp = min(min(byWidth, maxHeight), if (mode == GameMode.SLOTS) 96.dp else 132.dp)
                    .coerceAtLeast(0.dp)

                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    for (index in 0 until positions) {
                        val spinning = phase == DrawPhase.SPINNING ||
                                (phase == DrawPhase.REVEALING && index >= revealedSlots)
                        val finalIndex = finalSymbolIndex(result, index)
                        ReelBox(
                            symbols = if (mode == GameMode.SLOTS) SLOT_EMOJI else RPS_EMOJI,
                            finalIndex = finalIndex,
                            spinning = spinning,
                            seed = index * 3 + player.ordinal * 5,
                            boxSize = reelSize,
                        )
                    }
                }
            }

            MeasurementLine(mode = mode, phase = phase, revealedSlots = revealedSlots, result = result)
            OutcomeLine(phase = phase, result = result)
        }
    }
}

private fun finalSymbolIndex(result: PanelResult?, index: Int): Int? = when (result) {
    is SlotsPanel -> result.symbols.getOrNull(index)?.ordinal
    is RpsPanel -> result.move.ordinal
    null -> null
}

@Composable
private fun PanelHeader(name: String, sessionWins: Int, isWinner: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (isWinner) {
            Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primary) {
                Text(
                    text = stringResource(R.string.winner_chip),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                )
            }
        }
        Text(
            text = stringResource(R.string.session_wins, sessionWins),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Jeden bęben. Podczas kręcenia przewija symbole, po zatrzymaniu pokazuje wynik pomiaru. */
@Composable
private fun ReelBox(
    symbols: List<String>,
    finalIndex: Int?,
    spinning: Boolean,
    seed: Int,
    boxSize: Dp,
) {
    var frame by remember { mutableIntStateOf(seed) }
    LaunchedEffect(spinning) {
        while (spinning) {
            delay(SPIN_FRAME_MILLIS)
            frame++
        }
    }
    val shownIndex: Int? = if (spinning) frame % symbols.size else finalIndex
    val description = shownIndex?.let { symbols[it] } ?: "?"

    Box(
        modifier = Modifier
            .size(boxSize)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .semantics { contentDescription = if (spinning) "…" else description },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = shownIndex,
            transitionSpec = {
                if (spinning) {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                } else {
                    (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith fadeOut()
                }
            },
            label = "reel",
        ) { index ->
            Text(
                text = index?.let { symbols[it] } ?: "?",
                fontSize = (boxSize.value * 0.45f).sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** Zmierzone stany kubitów, np. |101⟩ |011⟩ |000⟩. W KPN odrzucone |11⟩ są przekreślone strzałką. */
@Composable
private fun MeasurementLine(mode: GameMode, phase: DrawPhase, revealedSlots: Int, result: PanelResult?) {
    val text = when {
        phase == DrawPhase.SPINNING -> stringResource(R.string.measuring)
        result == null -> if (mode == GameMode.SLOTS) "H|000⟩ ×3" else "H|00⟩"
        result is SlotsPanel -> result.kets.mapIndexed { i, ket ->
            if (phase == DrawPhase.REVEALING && i >= revealedSlots) "|···⟩" else ket
        }.joinToString("  ")
        result is RpsPanel -> {
            val shown = if (phase == DrawPhase.REVEALING && revealedSlots == 0) listOf("|··⟩") else result.kets
            shown.joinToString("  →  ")
        }
        else -> ""
    }
    Text(
        text = text,
        style = KetTextStyle,
        color = MaterialTheme.colorScheme.secondary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
    )
}

/** Opis wyniku po zatrzymaniu bębnów: „Para · 17 pkt” albo „Papier”. */
@Composable
private fun OutcomeLine(phase: DrawPhase, result: PanelResult?) {
    val text = if (phase != DrawPhase.REVEALED || result == null) {
        " "
    } else {
        when (result) {
            is SlotsPanel -> stringResource(
                R.string.hand_description,
                stringResource(result.hand.type.labelRes),
                result.hand.points,
            )
            is RpsPanel -> stringResource(result.move.labelRes)
        }
    }
    Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
}
