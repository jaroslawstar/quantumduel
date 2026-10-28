package pl.student.quantumduel.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Wiersz historii. Wyniki okienek zapisujemy jako nazwy enumów rozdzielone przecinkami,
 * np. "SEVEN,SEVEN,CHERRY" (automat) albo "ROCK" (KPN).
 */
@Entity(tableName = "rounds")
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long,
    val mode: String,
    val winner: String,
    val panelA: String,
    val panelB: String,
    val attempts: Int,
    val measuredQubits: Int,
    val origin: String,
)

@Dao
interface RoundDao {
    @Query("SELECT * FROM rounds ORDER BY timestampMillis DESC, id DESC")
    fun observeAll(): Flow<List<RoundEntity>>

    @Insert
    suspend fun insert(round: RoundEntity): Long

    @Query("DELETE FROM rounds")
    suspend fun clear()
}

@Database(entities = [RoundEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao

    companion object {
        const val NAME = "quantum_duel.db"
    }
}
