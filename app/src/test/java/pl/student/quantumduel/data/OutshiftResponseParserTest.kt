package pl.student.quantumduel.data

import org.junit.Ignore
import org.junit.Test

/** Testy parsera odpowiedzi Outshift. ETAP WSTĘPNY: testy zaplanowane i wyłączone (@Ignore). */
class OutshiftResponseParserTest {

    @Ignore("TODO: OutshiftResponseParser.parse")
    @Test
    fun parsesObjectsWithDecimalField() {
        // {"random_numbers":[{"decimal":"12"}, …]} → [12, …]
    }

    @Ignore("TODO: OutshiftResponseParser.parse")
    @Test
    fun parsesBinaryAndHexWhenDecimalMissing() {
        // pola binary / hexadecimal / octal
    }

    @Ignore("TODO: OutshiftResponseParser.parse")
    @Test
    fun parsesPlainNumbersAndStrings() {
        // {"data":[1,"2",3]} oraz sama tablica [7, 8]
    }

    @Ignore("TODO: OutshiftResponseParser.parse")
    @Test
    fun valuesAreTrimmedToOneByte() {
        // 256 → 0, 257 → 1
    }

    @Ignore("TODO: OutshiftResponseParser.parse")
    @Test
    fun missingArrayIsAnError() {
        // {"message":"Unauthorized"} → IllegalArgumentException
    }
}
