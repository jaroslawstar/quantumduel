package pl.student.quantumduel.data.entropy

import kotlinx.coroutines.flow.first
import pl.student.quantumduel.data.remote.AnuQrngApi
import pl.student.quantumduel.data.remote.OutshiftQrngApi
import pl.student.quantumduel.data.remote.OutshiftRequestDto
import pl.student.quantumduel.data.remote.OutshiftResponseParser
import pl.student.quantumduel.domain.entropy.EntropyOrigin
import pl.student.quantumduel.domain.settings.SettingsRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Zamienia kody HTTP na komunikaty zrozumiałe dla użytkownika. */
private fun HttpException.toReadableIOException(service: String): IOException = IOException(
    when (code()) {
        401, 403 -> "$service: nieprawidłowy lub brakujący klucz API (${code()})"
        429 -> "$service: wyczerpany limit zapytań (429)"
        in 500..599 -> "$service: błąd serwera (${code()})"
        else -> "$service: HTTP ${code()}"
    },
    this,
)

@Singleton
class AnuQrngProvider @Inject constructor(
    private val api: AnuQrngApi,
    private val settingsRepository: SettingsRepository,
) : QuantumEntropyProvider {

    override val origin = EntropyOrigin.ANU

    override suspend fun fetch(count: Int): List<Int> {
        val key = settingsRepository.settings.first().anuApiKey.trim()
        if (key.isEmpty()) throw IOException("ANU: brak klucza API")
        val response = try {
            api.randomNumbers(apiKey = key, length = count.coerceIn(1, AnuQrngApi.MAX_LENGTH))
        } catch (e: HttpException) {
            throw e.toReadableIOException("ANU")
        }
        if (!response.success) throw IOException("ANU: ${response.message ?: "odpowiedź bez danych"}")
        return response.data.map { it and 0xFF }
    }
}

@Singleton
class OutshiftQrngProvider @Inject constructor(
    private val api: OutshiftQrngApi,
    private val settingsRepository: SettingsRepository,
) : QuantumEntropyProvider {

    override val origin = EntropyOrigin.OUTSHIFT

    override suspend fun fetch(count: Int): List<Int> {
        val key = settingsRepository.settings.first().outshiftApiKey.trim()
        if (key.isEmpty()) throw IOException("Outshift: brak klucza API")
        val body = OutshiftRequestDto(
            encoding = "raw",
            format = "decimal",
            bitsPerBlock = 8,
            numberOfBlocks = count.coerceIn(1, OutshiftQrngApi.MAX_BLOCKS),
        )
        val response = try {
            api.randomNumbers(apiKey = key, body = body)
        } catch (e: HttpException) {
            throw e.toReadableIOException("Outshift")
        }
        return try {
            OutshiftResponseParser.parse(response)
        } catch (e: IllegalArgumentException) {
            throw IOException("Outshift: nieznany format odpowiedzi", e)
        }
    }
}
