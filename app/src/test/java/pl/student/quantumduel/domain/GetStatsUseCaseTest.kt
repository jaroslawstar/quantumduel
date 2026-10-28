package pl.student.quantumduel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Ignore
import org.junit.Test
import pl.student.quantumduel.domain.usecase.DuelStats

/**
 * Testy statystyk. ETAP WSTĘPNY: działa test modelu [DuelStats],
 * testy obliczeń są zaplanowane i wyłączone (@Ignore).
 */
class GetStatsUseCaseTest {

    @Test
    fun emptyStatsHaveZeroShares() {
        val stats = DuelStats()
        assertEquals(0.0, stats.winShareA, 0.0)
        assertEquals(0.0, stats.quantumShare, 0.0)
        assertEquals(0.0, stats.averageAttempts, 0.0)
        assertEquals(8, stats.slotSymbolCounts.size)
        assertNull(stats.slotsUniformity)
    }

    @Ignore("TODO: GetStatsUseCase")
    @Test
    fun emptyHistoryGivesZeros() {
        // pusta historia → DuelStats() bez testu χ²
    }

    @Ignore("TODO: GetStatsUseCase")
    @Test
    fun countsWinsMovesAndQuantumShare() {
        // wygrane A/B, udział rund kwantowych, średnia liczba prób, liczność symboli i ruchów
    }

    @Ignore("TODO: GetStatsUseCase")
    @Test
    fun chiSquareDetectsUniformAndSkewedData() {
        // równy rozkład → χ² = 0 (zgodny); same kamienie → χ² powyżej progu 5,991
    }
}
