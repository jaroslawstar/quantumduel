package pl.student.quantumduel.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.entropy.EntropyStatus
import java.util.Random

/**
 * Testowe źródło bajtów: najpierw wydaje bajty ze [script], potem losowe z ziarnem [seed].
 * Bajt < 128 daje pomiar 0, bajt ≥ 128 daje 1 (kubit po bramce H).
 */
class FakeEntropyRepository(
    script: List<Int> = emptyList(),
    seed: Long = 42L,
    private val origin: EntropyOrigin = EntropyOrigin.ANU,
) : EntropyRepository {

    private val scripted = ArrayDeque(script)
    private val random = Random(seed)
    var consumed = 0
        private set

    override val status: StateFlow<EntropyStatus> = MutableStateFlow(EntropyStatus())

    override suspend fun nextByte(): EntropyByte {
        consumed++
        val value = scripted.removeFirstOrNull() ?: random.nextInt(256)
        return EntropyByte(value, origin)
    }

    override fun prefetch(force: Boolean) = Unit

    companion object {
        const val ZERO = 0
        const val ONE = 255

        /** Zamienia napis bitów, np. "101", na bajty dające dokładnie te wyniki pomiaru. */
        fun bits(pattern: String): List<Int> = pattern.filter { it == '0' || it == '1' }
            .map { if (it == '0') ZERO else ONE }
    }
}
