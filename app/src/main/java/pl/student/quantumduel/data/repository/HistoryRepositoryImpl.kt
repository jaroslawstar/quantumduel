package pl.student.quantumduel.data.repository

import kotlinx.coroutines.flow.Flow
import pl.student.quantumduel.data.local.RoundDao
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.history.HistoryRepository
import pl.student.quantumduel.domain.history.RoundRecord
import javax.inject.Inject
import javax.inject.Singleton

/** Historia losowań w bazie Room. ETAP WSTĘPNY. */
@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: RoundDao,
) : HistoryRepository {

    override fun observeHistory(): Flow<List<RoundRecord>> {
        // TODO: dao.observeAll() i mapowanie wierszy przez toDomainOrNull()
        TODO("HistoryRepositoryImpl.observeHistory")
    }

    override suspend fun save(round: DuelRound) {
        // TODO: dao.insert(round.toEntity())
        TODO("HistoryRepositoryImpl.save")
    }

    override suspend fun clear() {
        // TODO: dao.clear()
        TODO("HistoryRepositoryImpl.clear")
    }
}
