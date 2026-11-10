package pl.student.quantumduel.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.student.quantumduel.R
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol
import pl.student.quantumduel.domain.history.PanelSummary
import pl.student.quantumduel.domain.history.RoundRecord
import pl.student.quantumduel.domain.usecase.DuelStats
import pl.student.quantumduel.domain.usecase.UniformityTest
import pl.student.quantumduel.presentation.HistoryUiState
import pl.student.quantumduel.presentation.HistoryViewModel
import pl.student.quantumduel.ui.components.OriginBadge
import pl.student.quantumduel.ui.components.emoji
import pl.student.quantumduel.ui.components.labelRes
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HistoryRoute(onBack: () -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onBack = onBack, onClearHistory = viewModel::onClearHistory)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(state: HistoryUiState, onBack: () -> Unit, onClearHistory: () -> Unit) {
    var confirmClear by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (state.records.isNotEmpty()) {
                        IconButton(onClick = { confirmClear = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.history_clear))
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.records.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { WinsCard(state.stats, state.playerAName, state.playerBName) }
                if (state.stats.slotsUniformity != null || state.stats.slotSymbolCounts.values.sum() > 0) {
                    item {
                        DistributionCard(
                            title = stringResource(R.string.distribution_slots),
                            rows = SlotSymbol.entries.map { Triple(it.emoji, stringResource(it.labelRes), state.stats.slotSymbolCounts[it] ?: 0) },
                            test = state.stats.slotsUniformity,
                        )
                    }
                }
                if (state.stats.rpsMoveCounts.values.sum() > 0) {
                    item {
                        DistributionCard(
                            title = stringResource(R.string.distribution_rps),
                            rows = RpsMove.entries.map { Triple(it.emoji, stringResource(it.labelRes), state.stats.rpsMoveCounts[it] ?: 0) },
                            test = state.stats.rpsUniformity,
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.history_recent),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(state.records, key = { it.id }) { record ->
                    RoundRow(record, state.playerAName, state.playerBName)
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text(stringResource(R.string.history_clear_title)) },
            text = { Text(stringResource(R.string.history_clear_text)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    onClearHistory()
                }) { Text(stringResource(R.string.history_clear_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

private fun percent(part: Int, total: Int): Int = if (total == 0) 0 else (part * 100.0 / total).roundToInt()

@Composable
private fun WinsCard(stats: DuelStats, nameA: String, nameB: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.stats_title), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$nameA  ${stats.winsA}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${stats.winsB}  $nameB",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            LinearProgressIndicator(
                progress = { stats.winShareA.toFloat() },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary,
            )
            Row(Modifier.fillMaxWidth()) {
                Text("${percent(stats.winsA, stats.totalRounds)}%", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("${percent(stats.winsB, stats.totalRounds)}%", style = MaterialTheme.typography.labelMedium)
            }
            Text(
                text = stringResource(
                    R.string.stats_summary,
                    stats.totalRounds,
                    (stats.quantumShare * 100).roundToInt(),
                    stats.averageAttempts,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Rozkład wylosowanych symboli + test χ², który pozwala ocenić równomierność losowania. */
@Composable
private fun DistributionCard(title: String, rows: List<Triple<String, String, Int>>, test: UniformityTest?) {
    val total = rows.sumOf { it.third }
    val max = rows.maxOfOrNull { it.third }?.coerceAtLeast(1) ?: 1
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.distribution_expected, 100.0 / rows.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            rows.forEach { (emoji, label, count) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(emoji, fontSize = 18.sp, modifier = Modifier.width(32.dp))
                    Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(80.dp), maxLines = 1)
                    LinearProgressIndicator(
                        progress = { count.toFloat() / max },
                        modifier = Modifier.weight(1f).height(8.dp),
                    )
                    Text(
                        text = "$count · ${percent(count, total)}%",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(72.dp),
                    )
                }
            }
            val verdict = when {
                test == null -> stringResource(R.string.chi_square_not_enough)
                test.looksUniform -> stringResource(R.string.chi_square_ok, test.chiSquare, test.criticalValue, test.degreesOfFreedom)
                else -> stringResource(R.string.chi_square_bad, test.chiSquare, test.criticalValue, test.degreesOfFreedom)
            }
            Text(
                text = verdict,
                style = MaterialTheme.typography.bodySmall,
                color = if (test != null && !test.looksUniform) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun RoundRow(record: RoundRecord, nameA: String, nameB: String) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd.MM HH:mm:ss") }
    val time = remember(record.timestampMillis) {
        formatter.format(Instant.ofEpochMilli(record.timestampMillis).atZone(ZoneId.systemDefault()))
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$time · ${stringResource(record.mode.labelRes)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                OriginBadge(record.origin)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                PanelText(record.panelA, isWinner = record.winner == Player.A, modifier = Modifier.weight(1f))
                Text("vs", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp))
                PanelText(record.panelB, isWinner = record.winner == Player.B, modifier = Modifier.weight(1f), alignEnd = true)
            }
            Text(
                text = stringResource(
                    R.string.history_row_summary,
                    if (record.winner == Player.A) nameA else nameB,
                    record.attempts,
                    record.measuredQubits,
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun PanelText(summary: PanelSummary, isWinner: Boolean, modifier: Modifier, alignEnd: Boolean = false) {
    val text = when (summary) {
        is PanelSummary.Slots -> summary.symbols.joinToString(" ") { it.emoji }
        is PanelSummary.Rps -> summary.move.emoji
    }
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        modifier = modifier,
        color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
}
