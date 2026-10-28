package pl.student.quantumduel.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * ANU Quantum Numbers – losowość z pomiaru kwantowych fluktuacji próżni (Australian National University).
 * GET https://api.quantumnumbers.anu.edu.au?length=1024&type=uint8, nagłówek x-api-key.
 */
interface AnuQrngApi {

    @GET("/")
    suspend fun randomNumbers(
        @Header("x-api-key") apiKey: String,
        @Query("length") length: Int,
        @Query("type") type: String = TYPE_UINT8,
    ): AnuResponseDto

    companion object {
        const val BASE_URL = "https://api.quantumnumbers.anu.edu.au/"
        const val TYPE_UINT8 = "uint8"
        const val MAX_LENGTH = 1024
    }
}

/** Odpowiedź ANU, np. {"success":true,"type":"uint8","length":1024,"data":[126,3,…]}. */
@Serializable
data class AnuResponseDto(
    val success: Boolean = false,
    val data: List<Int> = emptyList(),
    val message: String? = null,
)

/**
 * Outshift QRNG (Cisco) – zapasowe źródło kwantowe.
 * POST https://api.qrng.outshift.com/api/v1/random_numbers, nagłówek x-id-api-key.
 */
interface OutshiftQrngApi {

    @POST("api/v1/random_numbers")
    suspend fun randomNumbers(
        @Header("x-id-api-key") apiKey: String,
        @Body body: OutshiftRequestDto,
    ): JsonElement

    companion object {
        const val BASE_URL = "https://api.qrng.outshift.com/"
        const val MAX_BLOCKS = 1000
    }
}

@Serializable
data class OutshiftRequestDto(
    @SerialName("encoding") val encoding: String,
    @SerialName("format") val format: String,
    @SerialName("bits_per_block") val bitsPerBlock: Int,
    @SerialName("number_of_blocks") val numberOfBlocks: Int,
)
