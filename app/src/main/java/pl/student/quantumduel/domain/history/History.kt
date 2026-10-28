package pl.student.quantumduel.domain.history

import kotlinx.coroutines.flow.Flow
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.PanelResult
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol

/** Skrócony wynik okienka zapisywany w historii. */
sealed interface PanelSummary {
    data class Slots(val symbols: List<SlotSymbol>) : PanelSummary
    data class Rps(val move: RpsMove) : PanelSummary
}

/** Wynik okienka → skrót do historii. ETAP WSTĘPNY. */
fun PanelResult.toSummary(): PanelSummary {
    // TODO: SlotsPanel → PanelSummary.Slots(symbols), RpsPanel → PanelSummary.Rps(move)
    TODO("PanelResult.toSummary")
}

/** Jeden wpis historii losowań. */
data class RoundRecord(
    val id: Long,
    val timestampMillis: Long,
    val mode: GameMode,
    val winner: Player,
    val panelA: PanelSummary,
    val panelB: PanelSummary,
    val attempts: Int,
    val measuredQubits: Int,
    val origin: EntropyOrigin,
)

interface HistoryRepository {
    /** Historia od najnowszego wpisu. */
    fun observeHistory(): Flow<List<RoundRecord>>

    suspend fun save(round: DuelRound)

    suspend fun clear()
}
