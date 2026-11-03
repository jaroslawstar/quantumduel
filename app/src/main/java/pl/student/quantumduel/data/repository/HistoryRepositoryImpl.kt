package pl.student.quantumduel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.student.quantumduel.data.local.RoundDao
import pl.student.quantumduel.data.local.toDomainOrNull
import pl.student.quantumduel.data.local.toEntity
import pl.student.quantumduel.domain.game.DuelRound
import pl.student.quantumduel.domain.history.HistoryRepository
import pl.student.quantumduel.domain.history.RoundRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: RoundDao,
) : HistoryRepository {

    override fun observeHistory(): Flow<List<RoundRecord>> =
        dao.observeAll().map { rows -> rows.mapNotNull { it.toDomainOrNull() } }

    override suspend fun save(round: DuelRound) {
        dao.insert(round.toEntity())
    }

    override suspend fun clear() = dao.clear()
}
