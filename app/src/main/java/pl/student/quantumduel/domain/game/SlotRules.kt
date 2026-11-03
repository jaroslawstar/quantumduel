package pl.student.quantumduel.domain.game

/**
 * Symbol bębna. Indeks (0–7) to wynik pomiaru 3 kubitów: 2³ = 8 równie prawdopodobnych stanów.
 * [points] służy do rozstrzygania układów tego samego typu.
 */
enum class SlotSymbol(val points: Int) {
    CHERRY(1),
    LEMON(2),
    ORANGE(3),
    GRAPE(4),
    BELL(5),
    STAR(6),
    DIAMOND(7),
    SEVEN(8);

    companion object {
        fun fromIndex(index: Int): SlotSymbol = entries[index]
    }
}

/** Typ układu na bębnach – od najsłabszego do najmocniejszego. */
enum class SlotHandType(val rank: Int) {
    HIGH(0),
    PAIR(1),
    TRIPLE(2),
}

/**
 * Układ trzech symboli. Porównanie: trójka > para > brak układu;
 * przy tym samym typie wygrywa wyższa suma punktów. Równe układy = remis.
 */
data class SlotHand(val type: SlotHandType, val points: Int) : Comparable<SlotHand> {

    override fun compareTo(other: SlotHand): Int =
        compareValuesBy(this, other, { it.type.rank }, { it.points })

    companion object {
        fun of(symbols: List<SlotSymbol>): SlotHand {
            require(symbols.isNotEmpty()) { "Układ wymaga co najmniej jednego symbolu" }
            val maxSame = symbols.groupingBy { it }.eachCount().values.max()
            val type = when {
                maxSame >= 3 -> SlotHandType.TRIPLE
                maxSame == 2 -> SlotHandType.PAIR
                else -> SlotHandType.HIGH
            }
            return SlotHand(type, symbols.sumOf { it.points })
        }
    }
}
