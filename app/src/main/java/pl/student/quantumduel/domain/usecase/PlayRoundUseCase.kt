package pl.student.quantumduel.domain.usecase

import pl.student.quantumduel.domain.entropy.EntropyRepository
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.game.GameMode

/**
 * Jedna runda pojedynku z gwarantowanym zwycięzcą. ETAP WSTĘPNY.
 *
 * Plan:
 * 1. Dla każdego okienka utworzyć rejestr kubitów w |0…0⟩, nałożyć H na każdy kubit i zmierzyć.
 *    Automat: 3 bębny × 3 kubity. KPN: 2 kubity, wynik |11⟩ odrzucany i mierzony ponownie.
 * 2. Porównać wyniki przez [pl.student.quantumduel.domain.game.GameRules].
 * 3. Remis unieważnia pomiar obu okienek – mierzymy od nowa (szanse graczy pozostają 50/50).
 * 4. Zwrócić [DuelRound] z liczbą prób, liczbą zmierzonych kubitów i najsłabszym użytym źródłem losowości.
 */
class PlayRoundUseCase(
    private val entropy: EntropyRepository,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {

    suspend operator fun invoke(mode: GameMode): DuelRound {
        // TODO: pętla prób (maks. MAX_ATTEMPTS): zmierz okienko A i B, GameRules.winner(), przy remisie powtórz
        TODO("PlayRoundUseCase – runda z gwarantowanym zwycięzcą")
    }

    // TODO: measureSlots(source) – REELS bębnów, każdy z rejestru QUBITS_PER_REEL kubitów po bramce H
    // TODO: measureRps(source) – rejestr QUBITS_PER_RPS kubitów, odrzucanie wyniku |11⟩ (maks. MAX_RPS_MEASUREMENTS)
    // TODO: źródło bajtów zliczające zużyte bajty i zapamiętujące najsłabsze źródło (EntropyOrigin)

    companion object {
        const val REELS = 3
        const val QUBITS_PER_REEL = 3
        const val QUBITS_PER_RPS = 2
        const val MAX_ATTEMPTS = 1_000
        const val MAX_RPS_MEASUREMENTS = 1_000
    }
}
