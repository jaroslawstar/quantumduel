package pl.student.quantumduel.data

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import pl.student.quantumduel.data.entropy.EntropyPool
import pl.student.quantumduel.domain.entropy.EntropyByte
import pl.student.quantumduel.domain.entropy.EntropyOrigin

/**
 * Testy puli entropii. ETAP WSTĘPNY: działają testy konfiguracji i modelu bajtu,
 * testy działania puli są zaplanowane i wyłączone (@Ignore).
 */
class EntropyPoolTest {

    @Test
    fun defaultConfigMatchesDesign() {
        val config = EntropyPool.Config()
        assertEquals(1024, config.batchSize)
        assertEquals(256, config.refillThreshold)
        assertEquals(5_000L, config.requestTimeoutMillis)
        assertEquals(60_000L, config.failureCooldownMillis)
    }

    @Test(expected = IllegalArgumentException::class)
    fun entropyByteRejectsValuesOutsideByte() {
        EntropyByte(256, EntropyOrigin.ANU)
    }

    @Ignore("TODO: EntropyPool.nextByte")
    @Test
    fun servesQuantumBytesInOrderWithoutRepeats() {
        // bajty wydawane w kolejności pobrania, bez powtórzeń; pula dobiera partie w tle
    }

    @Ignore("TODO: EntropyPool – kolejność dostawców")
    @Test
    fun fallsBackToSecondProvider() {
        // pierwszy dostawca rzuca wyjątek → bajty od drugiego, błąd w status.lastError
    }

    @Ignore("TODO: EntropyPool – SecureRandom i karencja")
    @Test
    fun fallsBackToLocalAndRespectsCooldown() {
        // wszyscy dostawcy zawodzą → bajty LOCAL; w czasie karencji sieć nie jest odpytywana
    }

    @Ignore("TODO: EntropyPool")
    @Test
    fun noProvidersMeansLocalBytes() {
        // brak skonfigurowanych kluczy → bajty z SecureRandom
    }

    @Ignore("TODO: EntropyPool – limit czasu")
    @Test
    fun timeoutFallsBackToLocal() {
        // dostawca nie odpowiada w requestTimeoutMillis → bajt LOCAL
    }

    @Ignore("TODO: EntropyPool.prefetch")
    @Test
    fun forcedPrefetchFillsPool() {
        // prefetch(force = true) wypełnia pulę jedną partią
    }
}
