package pl.student.quantumduel.domain

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import pl.student.quantumduel.domain.quantum.QuantumRegister

/**
 * Testy symulatora kubitów. ETAP WSTĘPNY: działają testy stanu początkowego,
 * testy obliczeń są zaplanowane i wyłączone (@Ignore) do czasu implementacji.
 */
class QuantumRegisterTest {

    @Test
    fun newRegisterIsInZeroState() {
        val probabilities = QuantumRegister(3).probabilities()
        assertEquals(8, probabilities.size)
        assertEquals(1.0, probabilities[0], 1e-12)
        assertEquals(1.0, probabilities.sum(), 1e-12)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidQubitCount() {
        QuantumRegister(0)
    }

    @Ignore("TODO: QuantumRegister.applyToAll / probabilityOfZero")
    @Test
    fun hadamardCreatesEqualSuperposition() {
        // H⊗H⊗H|000⟩: każdy z 8 stanów ma prawdopodobieństwo 1/8, P(0) każdego kubitu = 0,5
    }

    @Ignore("TODO: QuantumRegister.apply")
    @Test
    fun hadamardTwiceReturnsToZero() {
        // H·H = I, więc po dwóch bramkach P(0) = 1
    }

    @Ignore("TODO: QuantumRegister.measure")
    @Test
    fun byteThresholdSplitsAt128() {
        // po H: bajty 0–127 → wynik 0, bajty 128–255 → wynik 1
    }

    @Ignore("TODO: QuantumRegister.measure")
    @Test
    fun measurementCollapsesState() {
        // po pomiarze kubitu 0 = 1: P(0) tego kubitu = 0, drugi kubit nadal 0,5, suma prawdopodobieństw = 1
    }

    @Ignore("TODO: QuantumRegister.measure")
    @Test
    fun deterministicStateIgnoresRandomness() {
        // X|0⟩ = |1⟩ – pomiar zawsze daje 1, nawet dla bajtu 0
    }

    @Ignore("TODO: QuantumRegister.measureAll")
    @Test
    fun threeQubitsGiveUniformEightOutcomes() {
        // każda kombinacja „niskich” i „wysokich” bajtów daje inny z 8 stanów
    }

    @Ignore("TODO: QuantumRegister.ket / toIndex")
    @Test
    fun ketAndIndexFormatting() {
        // [1, 0, 1] → "|101⟩" i 5
    }
}
