package pl.student.quantumduel.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.GameMode

// ETAP WSTĘPNY: wspólne elementy interfejsu.
// TODO: emoji i etykiety symboli automatu, ruchów KPN, układów i źródeł losowości
// TODO: plakietka źródła losowości OriginBadge („Kwantowe (ANU)” / „Lokalne (SecureRandom)”)

@get:StringRes
val GameMode.labelRes: Int
    get() = when (this) {
        GameMode.SLOTS -> R.string.mode_slots
        GameMode.RPS -> R.string.mode_rps
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

/** Treść zastępcza ekranu w budowie: krótki opis i lista planowanych funkcji. */
@Composable
fun PlannedFeatures(intro: String, items: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = intro, style = MaterialTheme.typography.bodyLarge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.planned_title), style = MaterialTheme.typography.titleMedium)
                items.forEach { item ->
                    Text(text = "•  $item", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
