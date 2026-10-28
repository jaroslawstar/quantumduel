package pl.student.quantumduel.domain

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import pl.student.quantumduel.domain.usecase.PlayRoundUseCase

/**
 * Testy rundy pojedynku. ETAP WSTĘPNY: działa test założeń (budżet kubitów),
 * testy przebiegu rundy są zaplanowane i wyłączone (@Ignore).
 * Do testów służy [FakeEntropyRepository] – podaje zaplanowane bajty (0 → pomiar 0, 255 → pomiar 1).
 */
class PlayRoundUseCaseTest {

    @Test
    fun slotsRoundBudgetIsEighteenQubits() {
        val perPanel = PlayRoundUseCase.REELS * PlayRoundUseCase.QUBITS_PER_REEL
        assertEquals(18, 2 * perPanel)
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun slotsRoundUsesEighteenQubitsWithoutTie() {
        // A: |111⟩×3 = trzy siódemki, B: wiśnia, wiśnia, cytryna → wygrywa A, 1 próba, 18 kubitów
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun tieTriggersRemeasurementOfBothPanels() {
        // identyczne okienka → remis → drugi pomiar obu okienek (2 próby, 36 kubitów)
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun rpsRejectsElevenAndMeasuresAgain() {
        // A: |11⟩ (odrzucone), potem |00⟩ = kamień; B: |10⟩ = nożyczki → wygrywa A
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun everyRoundHasAWinnerAndOddsAreFair() {
        // 4 000 rund w każdym trybie: zawsze jest zwycięzca, udział wygranych A ≈ 50% (±4 p.p.)
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun rpsMovesAreUniform() {
        // każdy ruch KPN pojawia się w ≈ 1/3 przypadków
    }

    @Ignore("TODO: PlayRoundUseCase")
    @Test
    fun weakestOriginIsReported() {
        // bajty z SecureRandom → runda oznaczona jako EntropyOrigin.LOCAL
    }
}
