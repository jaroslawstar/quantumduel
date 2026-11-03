package pl.student.quantumduel.domain.quantum

import pl.student.quantumduel.domain.entropy.ByteSource
import kotlin.math.sqrt

/**
 * Symulator rejestru [qubitCount] kubitów oparty na wektorze stanu (2^n amplitud zespolonych).
 *
 * Kubit 0 jest najbardziej znaczącym bitem, więc stan |q0 q1 q2⟩ czytamy od lewej.
 * Rejestr startuje w stanie |00…0⟩.
 *
 * Pomiar stosuje regułę Borna: P(0) = suma |α|² po stanach, w których dany kubit ma wartość 0.
 * Losowość pomiaru pochodzi z jednego bajtu b (0–255): u = b / 256, wynik 0 gdy u < P(0).
 * Po bramce Hadamarda P(0) = 0,5, więc bajty 0–127 dają 0, a 128–255 dają 1 – rozkład dokładnie równy.
 */
class QuantumRegister(val qubitCount: Int) {

    init {
        require(qubitCount in 1..MAX_QUBITS) { "Liczba kubitów musi być w zakresie 1–$MAX_QUBITS" }
    }

    private val dimension = 1 shl qubitCount
    private val amplitudes = Array(dimension) { index -> if (index == 0) Complex.ONE else Complex.ZERO }

    private fun mask(qubit: Int): Int {
        require(qubit in 0 until qubitCount) { "Brak kubitu o indeksie $qubit" }
        return 1 shl (qubitCount - 1 - qubit)
    }

    /** Nakłada bramkę na wybrany kubit. */
    fun apply(gate: Gate, qubit: Int): QuantumRegister {
        val m = mask(qubit)
        for (i in 0 until dimension) {
            if (i and m == 0) {
                val j = i or m
                val a0 = amplitudes[i]
                val a1 = amplitudes[j]
                amplitudes[i] = gate.m00 * a0 + gate.m01 * a1
                amplitudes[j] = gate.m10 * a0 + gate.m11 * a1
            }
        }
        return this
    }

    /** Nakłada tę samą bramkę na każdy kubit (np. H⊗H⊗H). */
    fun applyToAll(gate: Gate): QuantumRegister {
        for (q in 0 until qubitCount) apply(gate, q)
        return this
    }

    /** Prawdopodobieństwo, że pomiar kubitu da 0. */
    fun probabilityOfZero(qubit: Int): Double {
        val m = mask(qubit)
        var p = 0.0
        for (i in 0 until dimension) {
            if (i and m == 0) p += amplitudes[i].abs2
        }
        return p
    }

    /** Prawdopodobieństwa wszystkich stanów bazowych (suma = 1). */
    fun probabilities(): List<Double> = amplitudes.map { it.abs2 }

    fun amplitudesSnapshot(): List<Complex> = amplitudes.toList()

    /** Mierzy jeden kubit, zużywając jeden bajt ze źródła, i kolapsuje stan. */
    suspend fun measure(qubit: Int, source: ByteSource): Int {
        val p0 = probabilityOfZero(qubit)
        val u = (source.nextByte() and 0xFF) / 256.0
        val outcome = if (u < p0) 0 else 1
        collapse(qubit, outcome, if (outcome == 0) p0 else 1.0 - p0)
        return outcome
    }

    /** Mierzy kolejno wszystkie kubity; zwraca bity od kubitu 0. */
    suspend fun measureAll(source: ByteSource): List<Int> {
        val bits = ArrayList<Int>(qubitCount)
        for (q in 0 until qubitCount) bits += measure(q, source)
        return bits
    }

    private fun collapse(qubit: Int, outcome: Int, probability: Double) {
        val m = mask(qubit)
        val norm = 1.0 / sqrt(probability)
        for (i in 0 until dimension) {
            val bit = if (i and m == 0) 0 else 1
            amplitudes[i] = if (bit == outcome) amplitudes[i] * norm else Complex.ZERO
        }
    }

    companion object {
        const val MAX_QUBITS = 10

        /** Zapis Diraca, np. [1, 0, 1] → "|101⟩". */
        fun ket(bits: List<Int>): String = bits.joinToString(separator = "", prefix = "|", postfix = "⟩")

        /** Bity (kubit 0 = najstarszy) → liczba, np. [1, 0, 1] → 5. */
        fun toIndex(bits: List<Int>): Int = bits.fold(0) { acc, bit -> (acc shl 1) or bit }
    }
}
