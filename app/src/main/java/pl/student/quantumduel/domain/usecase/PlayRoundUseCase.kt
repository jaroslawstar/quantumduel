package pl.student.quantumduel.domain.usecase

import pl.student.quantumduel.domain.entropy.ByteSource
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode
import pl.student.quantumduel.domain.game.GameRules
import pl.student.quantumduel.domain.game.PanelResult
import pl.student.quantumduel.domain.game.RpsMove
import pl.student.quantumduel.domain.game.RpsPanel
import pl.student.quantumduel.domain.game.SlotSymbol
import pl.student.quantumduel.domain.game.SlotsPanel
import pl.student.quantumduel.domain.quantum.Gates
import pl.student.quantumduel.domain.quantum.QuantumRegister

/**
 * Jedna runda pojedynku z gwarantowanym zwycięzcą.
 *
 * 1. Dla każdego okienka tworzy rejestr kubitów w |0…0⟩, nakłada H na każdy kubit i mierzy.
 * 2. [GameRules] porównuje wyniki.
 * 3. Remis unieważnia pomiar obu okienek i całość jest powtarzana. Ponowienie jest symetryczne,
 *    więc szansa każdego gracza pozostaje równa 50%.
 */
class PlayRoundUseCase(
    private val entropy: EntropyRepository,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {

    suspend operator fun invoke(mode: GameMode): DuelRound {
        val source = TrackingByteSource(entropy)
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            attempts++
            val panelA = measurePanel(mode, source)
            val panelB = measurePanel(mode, source)
            val winner = GameRules.winner(panelA, panelB) ?: continue
            return DuelRound(
                mode = mode,
                panelA = panelA,
                panelB = panelB,
                winner = winner,
                attempts = attempts,
                measuredQubits = source.consumed,
                origin = source.weakestOrigin,
                timestampMillis = clock(),
            )
        }
        // Przy uczciwym źródle prawdopodobieństwo 1000 remisów z rzędu jest praktycznie zerowe.
        throw IllegalStateException("Nie udało się rozstrzygnąć pojedynku po $MAX_ATTEMPTS pomiarach")
    }

    private suspend fun measurePanel(mode: GameMode, source: ByteSource): PanelResult = when (mode) {
        GameMode.SLOTS -> measureSlots(source)
        GameMode.RPS -> measureRps(source)
    }

    private suspend fun measureSlots(source: ByteSource): SlotsPanel {
        val symbols = ArrayList<SlotSymbol>(REELS)
        val kets = ArrayList<String>(REELS)
        repeat(REELS) {
            val bits = QuantumRegister(QUBITS_PER_REEL).applyToAll(Gates.H).measureAll(source)
            symbols += SlotSymbol.fromIndex(QuantumRegister.toIndex(bits))
            kets += QuantumRegister.ket(bits)
        }
        return SlotsPanel(symbols, kets)
    }

    private suspend fun measureRps(source: ByteSource): RpsPanel {
        val kets = ArrayList<String>()
        repeat(MAX_RPS_MEASUREMENTS) {
            val bits = QuantumRegister(QUBITS_PER_RPS).applyToAll(Gates.H).measureAll(source)
            kets += QuantumRegister.ket(bits)
            val move = RpsMove.fromIndexOrNull(QuantumRegister.toIndex(bits))
            if (move != null) return RpsPanel(move, kets)
            // |11⟩ – wynik odrzucony, mierzymy ponownie.
        }
        throw IllegalStateException("Zbyt wiele odrzuconych pomiarów |11⟩")
    }

    /** Zlicza zużyte bajty i zapamiętuje najsłabsze użyte źródło. */
    private class TrackingByteSource(private val entropy: EntropyRepository) : ByteSource {
        var consumed = 0
            private set
        var weakestOrigin = EntropyOrigin.ANU
            private set

        override suspend fun nextByte(): Int {
            val byte = entropy.nextByte()
            consumed++
            if (byte.origin.ordinal > weakestOrigin.ordinal) weakestOrigin = byte.origin
            return byte.value
        }
    }

    companion object {
        const val REELS = 3
        const val QUBITS_PER_REEL = 3
        const val QUBITS_PER_RPS = 2
        const val MAX_ATTEMPTS = 1_000
        const val MAX_RPS_MEASUREMENTS = 1_000
    }
}
