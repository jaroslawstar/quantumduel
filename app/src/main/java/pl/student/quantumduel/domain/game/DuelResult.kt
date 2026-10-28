package pl.student.quantumduel.domain.game

import pl.student.quantumduel.domain.entropy.EntropyOrigin

/** Wynik jednego okienka po pomiarze kubitów. [kets] to zmierzone stany, np. "|101⟩". */
sealed interface PanelResult {
    val kets: List<String>
}

/** Okienko automatu: 3 symbole, każdy z pomiaru 3 kubitów. */
data class SlotsPanel(
    val symbols: List<SlotSymbol>,
    override val kets: List<String>,
) : PanelResult {
    val hand: SlotHand get() = SlotHand.of(symbols)
}

/**
 * Okienko KPN. [kets] zawiera wszystkie pomiary, łącznie z odrzuconymi |11⟩;
 * ostatni element to pomiar, który dał [move].
 */
data class RpsPanel(
    val move: RpsMove,
    override val kets: List<String>,
) : PanelResult {
    val rejectedMeasurements: Int get() = kets.size - 1
}

/**
 * Rozstrzygnięty pojedynek.
 *
 * @param attempts ile razy mierzono oba okienka (1 = bez remisu, 2 = jeden remis itd.)
 * @param measuredQubits ile pomiarów kubitów (= zużytych bajtów) wymagała runda
 * @param origin najsłabsze źródło użyte w rundzie (LOCAL, jeśli choć jeden bajt był lokalny)
 */
data class DuelRound(
    val mode: GameMode,
    val panelA: PanelResult,
    val panelB: PanelResult,
    val winner: Player,
    val attempts: Int,
    val measuredQubits: Int,
    val origin: EntropyOrigin,
    val timestampMillis: Long,
) {
    val ties: Int get() = attempts - 1

    fun panelOf(player: Player): PanelResult = if (player == Player.A) panelA else panelB
}
