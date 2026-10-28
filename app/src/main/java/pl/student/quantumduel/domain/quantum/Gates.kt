package pl.student.quantumduel.domain.quantum

import kotlin.math.sqrt

/**
 * Jednokubitowa bramka kwantowa jako macierz unitarna 2×2:
 *
 *     | m00  m01 |
 *     | m10  m11 |
 */
class Gate(
    val name: String,
    val m00: Complex,
    val m01: Complex,
    val m10: Complex,
    val m11: Complex,
)

object Gates {
    private val INV_SQRT2 = 1.0 / sqrt(2.0)

    /** Bramka Hadamarda: |0⟩ → (|0⟩ + |1⟩)/√2, czyli równa superpozycja. */
    val H = Gate(
        name = "H",
        m00 = Complex(INV_SQRT2), m01 = Complex(INV_SQRT2),
        m10 = Complex(INV_SQRT2), m11 = Complex(-INV_SQRT2),
    )

    /** Bramka NOT (Pauli-X): zamienia |0⟩ i |1⟩. */
    val X = Gate(
        name = "X",
        m00 = Complex.ZERO, m01 = Complex.ONE,
        m10 = Complex.ONE, m11 = Complex.ZERO,
    )
}
