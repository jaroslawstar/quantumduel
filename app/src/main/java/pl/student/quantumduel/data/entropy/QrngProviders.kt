package pl.student.quantumduel.data.entropy

import pl.student.quantumduel.data.remote.AnuQrngApi
import pl.student.quantumduel.data.remote.OutshiftQrngApi
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

// TODO: wspólna funkcja zamieniająca HttpException na czytelny IOException
//       (401/403 – zły klucz, 429 – wyczerpany limit, 5xx – błąd serwera)

/** Dostawca bajtów z ANU Quantum Numbers. ETAP WSTĘPNY. */
@Singleton
class AnuQrngProvider @Inject constructor(
    private val api: AnuQrngApi,
    private val settingsRepository: SettingsRepository,
) : QuantumEntropyProvider {

    override val origin = EntropyOrigin.ANU

    override suspend fun fetch(count: Int): List<Int> {
        // TODO: klucz z ustawień (pusty → IOException), api.randomNumbers(key, count w zakresie 1–1024),
        //       success = false → IOException, wynik: data obcięte do bajtów
        TODO("AnuQrngProvider.fetch")
    }
}

/** Dostawca bajtów z Outshift QRNG (Cisco) – źródło zapasowe. ETAP WSTĘPNY. */
@Singleton
class OutshiftQrngProvider @Inject constructor(
    private val api: OutshiftQrngApi,
    private val settingsRepository: SettingsRepository,
) : QuantumEntropyProvider {

    override val origin = EntropyOrigin.OUTSHIFT

    override suspend fun fetch(count: Int): List<Int> {
        // TODO: klucz z ustawień, zapytanie OutshiftRequestDto(encoding = "raw", format = "decimal",
        //       bitsPerBlock = 8, numberOfBlocks = count ≤ 1000), odpowiedź przez OutshiftResponseParser
        TODO("OutshiftQrngProvider.fetch")
    }
}
