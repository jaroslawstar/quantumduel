package pl.student.quantumduel.data.remote

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Elastyczny parser odpowiedzi Outshift QRNG.
 *
 * Publiczna dokumentacja nie gwarantuje jednego kształtu odpowiedzi, dlatego parser akceptuje:
 * - tablicę pod kluczem `random_numbers` (lub `data` / `numbers`),
 * - elementy będące liczbą, napisem z liczbą albo obiektem z polem `decimal`, `binary`, `hexadecimal` lub `octal`.
 *
 * Każda wartość jest obcinana do jednego bajtu (0–255), bo zamawiamy bloki 8-bitowe.
 */
object OutshiftResponseParser {

    private val ARRAY_KEYS = listOf("random_numbers", "data", "numbers")

    fun parse(root: JsonElement): List<Int> {
        val array = findArray(root) ?: throw IllegalArgumentException("Brak tablicy liczb w odpowiedzi Outshift")
        return array.mapNotNull { parseValue(it) }.map { it and 0xFF }
    }

    private fun findArray(root: JsonElement): JsonArray? = when (root) {
        is JsonArray -> root
        is JsonObject -> ARRAY_KEYS.firstNotNullOfOrNull { key -> root[key] as? JsonArray }
        else -> null
    }

    private fun parseValue(element: JsonElement): Int? = when (element) {
        is JsonNull -> null
        is JsonPrimitive -> parseNumber(element.content, radix = 10)
        is JsonObject -> {
            val decimal = (element["decimal"] as? JsonPrimitive)?.content
            val binary = (element["binary"] as? JsonPrimitive)?.content
            val hex = (element["hexadecimal"] as? JsonPrimitive)?.content
            val octal = (element["octal"] as? JsonPrimitive)?.content
            decimal?.let { parseNumber(it, 10) }
                ?: binary?.let { parseNumber(it, 2) }
                ?: hex?.let { parseNumber(it.removePrefix("0x"), 16) }
                ?: octal?.let { parseNumber(it, 8) }
        }
        is JsonArray -> null
    }

    private fun parseNumber(text: String, radix: Int): Int? =
        text.trim().toLongOrNull(radix)?.let { (it and 0xFF).toInt() }
}
