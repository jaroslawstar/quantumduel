package pl.student.quantumduel.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotHandType
import pl.student.quantumduel.domain.game.SlotSymbol

// --- Symbole i etykiety (warstwa UI – domena nie zna emoji ani tekstów) ---

val SlotSymbol.emoji: String
    get() = when (this) {
        SlotSymbol.CHERRY -> "🍒"
        SlotSymbol.LEMON -> "🍋"
        SlotSymbol.ORANGE -> "🍊"
        SlotSymbol.GRAPE -> "🍇"
        SlotSymbol.BELL -> "🔔"
        SlotSymbol.STAR -> "⭐"
        SlotSymbol.DIAMOND -> "💎"
        SlotSymbol.SEVEN -> "7️⃣"
    }

@get:StringRes
val SlotSymbol.labelRes: Int
    get() = when (this) {
        SlotSymbol.CHERRY -> R.string.symbol_cherry
        SlotSymbol.LEMON -> R.string.symbol_lemon
        SlotSymbol.ORANGE -> R.string.symbol_orange
        SlotSymbol.GRAPE -> R.string.symbol_grape
        SlotSymbol.BELL -> R.string.symbol_bell
        SlotSymbol.STAR -> R.string.symbol_star
        SlotSymbol.DIAMOND -> R.string.symbol_diamond
        SlotSymbol.SEVEN -> R.string.symbol_seven
    }

val RpsMove.emoji: String
    get() = when (this) {
        RpsMove.ROCK -> "✊"
        RpsMove.PAPER -> "✋"
        RpsMove.SCISSORS -> "✌️"
    }

@get:StringRes
val RpsMove.labelRes: Int
    get() = when (this) {
        RpsMove.ROCK -> R.string.move_rock
        RpsMove.PAPER -> R.string.move_paper
        RpsMove.SCISSORS -> R.string.move_scissors
    }

@get:StringRes
val SlotHandType.labelRes: Int
    get() = when (this) {
        SlotHandType.TRIPLE -> R.string.hand_triple
        SlotHandType.PAIR -> R.string.hand_pair
        SlotHandType.HIGH -> R.string.hand_high
    }

@get:StringRes
val GameMode.labelRes: Int
    get() = when (this) {
        GameMode.SLOTS -> R.string.mode_slots
        GameMode.RPS -> R.string.mode_rps
    }

@get:StringRes
val EntropyOrigin.labelRes: Int
    get() = when (this) {
        EntropyOrigin.ANU -> R.string.origin_anu
        EntropyOrigin.OUTSHIFT -> R.string.origin_outshift
        EntropyOrigin.LOCAL -> R.string.origin_local
    }

// --- Wspólne komponenty ---

/** Plakietka źródła losowości: „Kwantowe (ANU)” albo „Lokalne (SecureRandom)”. */
@Composable
fun OriginBadge(origin: EntropyOrigin, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (origin.isQuantum) colors.secondaryContainer else colors.surfaceVariant,
        contentColor = if (origin.isQuantum) colors.onSecondaryContainer else colors.onSurfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = if (origin.isQuantum) "⚛" else "🔒", style = MaterialTheme.typography.labelMedium)
            Text(text = stringResource(origin.labelRes), style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Przełącznik trybu gry (automat / KPN). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelector(
    selected: GameMode,
    onSelect: (GameMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val modes = GameMode.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = mode == selected,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                enabled = enabled,
                label = { Text(stringResource(mode.labelRes)) },
            )
        }
    }
}
