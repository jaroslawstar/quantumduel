package pl.student.quantumduel.domain.usecase

import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol
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

/** Liczy statystyki z historii – pozwala sprawdzić, czy wyniki są równomierne. ETAP WSTĘPNY. */
class GetStatsUseCase {

    operator fun invoke(records: List<RoundRecord>): DuelStats {
        // TODO: wygrane A/B, rundy kwantowe (origin.isQuantum), suma prób, liczność każdego symbolu i ruchu
        // TODO: test χ² = Σ (O − E)² / E dla symboli i ruchów, liczony tylko gdy oczekiwana liczność E ≥ 5
        TODO("GetStatsUseCase")
    }

    companion object {
        /** Wartości krytyczne χ² dla α = 0,05: 7 stopni swobody (8 symboli) i 2 (3 ruchy). */
        const val SLOTS_CRITICAL = 14.067
        const val RPS_CRITICAL = 5.991
    }
}
