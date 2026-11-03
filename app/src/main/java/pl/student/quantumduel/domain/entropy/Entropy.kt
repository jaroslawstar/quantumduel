package pl.student.quantumduel.domain.entropy

import kotlinx.coroutines.flow.StateFlow

/** Skąd pochodzi bajt losowości. Kolejność = od „najlepszego” do „najsłabszego” źródła. */
enum class EntropyOrigin(val isQuantum: Boolean) {
    ANU(isQuantum = true),
    OUTSHIFT(isQuantum = true),
    LOCAL(isQuantum = false),
}

/** Jeden bajt losowości (0–255) wraz ze źródłem. */
data class EntropyByte(val value: Int, val origin: EntropyOrigin) {
    init {
        require(value in 0..255) { "Bajt musi być w zakresie 0–255, otrzymano $value" }
    }
}

/** Stan puli entropii pokazywany w interfejsie. */
data class EntropyStatus(
    val pooledBytes: Int = 0,
    val lastOrigin: EntropyOrigin? = null,
    val isFetching: Boolean = false,
    val lastError: String? = null,
    val quantumConfigured: Boolean = false,
)

/** Dowolne źródło kolejnych bajtów – używane przez symulator kubitów do pomiaru. */
interface ByteSource {
    suspend fun nextByte(): Int
}

/** Kontrakt warstwy danych: dostarcza bajty z QRNG (lub lokalnie, gdy brak sieci). */
interface EntropyRepository {
    val status: StateFlow<EntropyStatus>

    /** Zwraca kolejny, nieużyty wcześniej bajt. Nigdy nie rzuca wyjątku – w razie problemów przełącza się na SecureRandom. */
    suspend fun nextByte(): EntropyByte

    /** Pobiera partię bajtów w tle. [force] = pomiń karencję po błędzie i pobierz nawet przy pełnej puli. */
    fun prefetch(force: Boolean = false)
}
