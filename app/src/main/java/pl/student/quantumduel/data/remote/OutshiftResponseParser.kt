package pl.student.quantumduel.data.remote

import kotlinx.serialization.json.JsonElement

/**
 * Parser odpowiedzi Outshift QRNG. ETAP WSTĘPNY.
 *
 * Publiczna dokumentacja nie gwarantuje jednego kształtu odpowiedzi, dlatego parser ma akceptować:
 * - tablicę pod kluczem `random_numbers` (lub `data` / `numbers`) albo samą tablicę,
 * - elementy będące liczbą, napisem z liczbą albo obiektem z polem `decimal`, `binary`, `hexadecimal` lub `octal`.
 *
 * Każda wartość ma być obcięta do jednego bajtu (0–255), bo zamawiamy bloki 8-bitowe.
 */
object OutshiftResponseParser {

    fun parse(root: JsonElement): List<Int> {
        // TODO: znaleźć tablicę liczb i zamienić elementy na bajty; brak tablicy → IllegalArgumentException
        TODO("OutshiftResponseParser.parse")
    }
}
