package pl.student.quantumduel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Ignore
import org.junit.Test
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.SlotSymbol

/**
 * Testy reguł gry. ETAP WSTĘPNY: działają testy modeli (liczba symboli i ruchów),
 * testy reguł są zaplanowane i wyłączone (@Ignore).
 */
class GameRulesTest {

    @Test
    fun slotSymbolsMatchThreeQubits() {
        assertEquals(8, SlotSymbol.entries.size) // 2³ stanów 3 kubitów
        assertEquals((1..8).toList(), SlotSymbol.entries.map { it.points })
        assertEquals(SlotSymbol.SEVEN, SlotSymbol.fromIndex(7))
    }

    @Test
    fun rpsMovesMatchTwoQubits() {
        assertEquals(3, RpsMove.entries.size)
        assertEquals(RpsMove.SCISSORS, RpsMove.fromIndexOrNull(2))
        assertNull(RpsMove.fromIndexOrNull(3)) // |11⟩ – wynik odrzucany
    }

    @Ignore("TODO: SlotHand.of")
    @Test
    fun handTypesAreRecognised() {
        // trzy takie same → TRIPLE, dwa → PAIR, wszystkie różne → HIGH; punkty = suma
    }

    @Ignore("TODO: SlotHand.compareTo / GameRules.winner")
    @Test
    fun tripleBeatsPairBeatsHigh() {
        // najsłabsza trójka bije najmocniejszą parę, najsłabsza para bije najmocniejszy brak układu
    }

    @Ignore("TODO: SlotHand.compareTo")
    @Test
    fun sameTypeComparesPoints() {
        // przy tym samym typie układu wygrywa wyższa suma punktów
    }

    @Ignore("TODO: GameRules.winner")
    @Test
    fun equalHandsAreATie() {
        // ten sam typ i suma punktów → null (remis)
    }

    @Ignore("TODO: RpsMove.beats / GameRules.winner")
    @Test
    fun rockPaperScissorsMatrix() {
        // wszystkie 9 par ruchów: ten sam ruch = remis, w pozostałych wygrywa ruch, który bije drugi
    }

    @Ignore("TODO: GameRules.winner")
    @Test
    fun mixedModesAreRejected() {
        // okienko automatu vs okienko KPN → IllegalArgumentException
    }
}
