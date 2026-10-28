package pl.student.quantumduel.domain.quantum

import kotlin.math.abs

/** Liczba zespolona – amplituda stanu kubitu. */
data class Complex(val re: Double, val im: Double = 0.0) {

    operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)

    operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)

    operator fun times(other: Complex) = Complex(
        re * other.re - im * other.im,
        re * other.im + im * other.re,
    )

    operator fun times(scalar: Double) = Complex(re * scalar, im * scalar)

    /** |z|² – kwadrat modułu, czyli prawdopodobieństwo według reguły Borna. */
    val abs2: Double get() = re * re + im * im

    fun isApprox(other: Complex, eps: Double = 1e-9): Boolean =
        abs(re - other.re) < eps && abs(im - other.im) < eps

    override fun toString(): String = when {
        im == 0.0 -> "%.3f".format(re)
        re == 0.0 -> "%.3fi".format(im)
        else -> "%.3f%+.3fi".format(re, im)
    }

    companion object {
        val ZERO = Complex(0.0, 0.0)
        val ONE = Complex(1.0, 0.0)
    }
}
