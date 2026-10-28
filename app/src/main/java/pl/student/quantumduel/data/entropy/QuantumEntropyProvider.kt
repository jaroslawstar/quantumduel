package pl.student.quantumduel.data.entropy

import pl.student.quantumduel.domain.entropy.EntropyOrigin
import java.security.SecureRandom
import java.util.Random

/** Zdalny kwantowy generator liczb losowych (QRNG). */
interface QuantumEntropyProvider {
    val origin: EntropyOrigin

    /** Pobiera do [count] bajtów (0–255). Rzuca wyjątek przy błędzie sieci lub odpowiedzi. */
    suspend fun fetch(count: Int): List<Int>
}

/** Lokalne źródło: kryptograficzny generator systemu – nieprzewidywalny, ale nie kwantowy. */
class LocalEntropySource(private val random: Random = SecureRandom()) {
    fun nextByte(): Int = random.nextInt(256)
}
