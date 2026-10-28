package pl.student.quantumduel.domain.quantum

import pl.student.quantumduel.domain.entropy.ByteSource

/**
 * Symulator rejestru [qubitCount] kubitów oparty na wektorze stanu (2^n amplitud zespolonych).
 *
 * ETAP WSTĘPNY: struktura danych i sygnatury są gotowe, obliczenia są do zaimplementowania.
 *
 * Założenia:
 * - kubit 0 jest najbardziej znaczącym bitem, więc stan |q0 q1 q2⟩ czytamy od lewej,
 * - rejestr startuje w stanie |00…0⟩,
 * - pomiar stosuje regułę Borna: bajt b (0–255) z puli entropii daje u = b / 256, a wynik to 0, gdy u < P(0),
 * - po pomiarze stan kolapsuje: amplitudy niezgodne z wynikiem są zerowane, pozostałe normalizowane.
 */
class QuantumRegister(val qubitCount: Int) {

    init {
        require(qubitCount in 1..MAX_QUBITS) { "Liczba kubitów musi być w zakresie 1–$MAX_QUBITS" }
    }

    private val dimension = 1 shl qubitCount

    /** Wektor stanu: jedna amplituda na każdy z 2^n stanów bazowych. Start: |00…0⟩. */
    private val amplitudes = Array(dimension) { index -> if (index == 0) Complex.ONE else Complex.ZERO }

    /** Nakłada bramkę na wybrany kubit. */
    fun apply(gate: Gate, qubit: Int): QuantumRegister {
        // TODO: dla każdej pary amplitud różniących się tylko bitem [qubit] pomnożyć je przez macierz bramki 2×2
        TODO("QuantumRegister.apply – mnożenie wektora stanu przez bramkę")
    }

    /** Nakłada tę samą bramkę na każdy kubit (np. H⊗H⊗H). */
    fun applyToAll(gate: Gate): QuantumRegister {
        // TODO: wywołać apply(gate, q) dla każdego kubitu q
        TODO("QuantumRegister.applyToAll")
    }

    /** Prawdopodobieństwo, że pomiar kubitu da 0. */
    fun probabilityOfZero(qubit: Int): Double {
        // TODO: suma |α|² po stanach bazowych, w których bit [qubit] ma wartość 0
        TODO("QuantumRegister.probabilityOfZero")
    }

    /** Prawdopodobieństwa wszystkich stanów bazowych (suma = 1). */
    fun probabilities(): List<Double> = amplitudes.map { it.abs2 }

    fun amplitudesSnapshot(): List<Complex> = amplitudes.toList()

    /** Mierzy jeden kubit, zużywając jeden bajt ze źródła, i kolapsuje stan. */
    suspend fun measure(qubit: Int, source: ByteSource): Int {
        // TODO: u = source.nextByte() / 256.0; wynik = if (u < P(0)) 0 else 1; potem kolaps i normalizacja stanu
        TODO("QuantumRegister.measure – pomiar według reguły Borna")
    }

    /** Mierzy kolejno wszystkie kubity; zwraca bity od kubitu 0. */
    suspend fun measureAll(source: ByteSource): List<Int> {
        // TODO: measure(q, source) dla każdego kubitu, wyniki w kolejności od kubitu 0
        TODO("QuantumRegister.measureAll")
    }

    companion object {
        const val MAX_QUBITS = 10

        /** Zapis Diraca, np. [1, 0, 1] → "|101⟩". */
        fun ket(bits: List<Int>): String = TODO("QuantumRegister.ket")

        /** Bity (kubit 0 = najstarszy) → liczba, np. [1, 0, 1] → 5. */
        fun toIndex(bits: List<Int>): Int = TODO("QuantumRegister.toIndex")
    }
}
