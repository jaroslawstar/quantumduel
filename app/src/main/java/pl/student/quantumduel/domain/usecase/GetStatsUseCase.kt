package pl.student.quantumduel.domain.usecase

import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.Player
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol
import pl.student.quantumduel.domain.history.PanelSummary
import pl.student.quantumduel.domain.history.RoundRecord

/** Test zgodności χ² z rozkładem równomiernym. */
data class UniformityTest(
    val chiSquare: Double,
    val degreesOfFreedom: Int,
    val criticalValue: Double,
) {
    /** true = brak podstaw do odrzucenia hipotezy o równomierności (p > 0,05). */
    val looksUniform: Boolean get() = chiSquare <= criticalValue
}

data class DuelStats(
    val totalRounds: Int = 0,
    val winsA: Int = 0,
    val winsB: Int = 0,
    val quantumRounds: Int = 0,
    val totalAttempts: Int = 0,
    val slotSymbolCounts: Map<SlotSymbol, Int> = SlotSymbol.entries.associateWith { 0 },
    val rpsMoveCounts: Map<RpsMove, Int> = RpsMove.entries.associateWith { 0 },
    val slotsUniformity: UniformityTest? = null,
    val rpsUniformity: UniformityTest? = null,
) {
    val winShareA: Double get() = if (totalRounds == 0) 0.0 else winsA.toDouble() / totalRounds
    val quantumShare: Double get() = if (totalRounds == 0) 0.0 else quantumRounds.toDouble() / totalRounds
    val averageAttempts: Double get() = if (totalRounds == 0) 0.0 else totalAttempts.toDouble() / totalRounds
}

/** Liczy statystyki z historii – pozwala sprawdzić, czy wyniki są równomierne. */
class GetStatsUseCase {

    operator fun invoke(records: List<RoundRecord>): DuelStats {
        if (records.isEmpty()) return DuelStats()

        val slotCounts = SlotSymbol.entries.associateWith { 0 }.toMutableMap()
        val rpsCounts = RpsMove.entries.associateWith { 0 }.toMutableMap()

        fun count(summary: PanelSummary) {
            when (summary) {
                is PanelSummary.Slots -> summary.symbols.forEach { slotCounts[it] = slotCounts.getValue(it) + 1 }
                is PanelSummary.Rps -> rpsCounts[summary.move] = rpsCounts.getValue(summary.move) + 1
            }
        }

        records.forEach {
            count(it.panelA)
            count(it.panelB)
        }

        return DuelStats(
            totalRounds = records.size,
            winsA = records.count { it.winner == Player.A },
            winsB = records.count { it.winner == Player.B },
            quantumRounds = records.count { it.origin.isQuantum },
            totalAttempts = records.sumOf { it.attempts },
            slotSymbolCounts = slotCounts,
            rpsMoveCounts = rpsCounts,
            slotsUniformity = if (records.any { it.mode == GameMode.SLOTS }) uniformity(slotCounts.values, SLOTS_CRITICAL) else null,
            rpsUniformity = if (records.any { it.mode == GameMode.RPS }) uniformity(rpsCounts.values, RPS_CRITICAL) else null,
        )
    }

    /**
     * χ² = Σ (O − E)² / E. Liczymy tylko, gdy oczekiwana liczność w każdej kategorii wynosi co najmniej 5
     * (klasyczny warunek stosowalności testu).
     */
    private fun uniformity(counts: Collection<Int>, critical: Double): UniformityTest? {
        val total = counts.sum()
        val expected = total.toDouble() / counts.size
        if (expected < MIN_EXPECTED) return null
        val chi = counts.sumOf { (it - expected) * (it - expected) / expected }
        return UniformityTest(chi, counts.size - 1, critical)
    }

    companion object {
        private const val MIN_EXPECTED = 5.0

        /** Wartości krytyczne χ² dla α = 0,05: 7 stopni swobody (8 symboli) i 2 (3 ruchy). */
        const val SLOTS_CRITICAL = 14.067
        const val RPS_CRITICAL = 5.991
    }
}
