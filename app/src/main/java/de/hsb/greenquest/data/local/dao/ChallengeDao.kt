package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(challenge: LocalChallengeEntity)

    @Update
    suspend fun update(challenge: LocalChallengeEntity)

    @Delete
    suspend fun delete(challenge: LocalChallengeEntity)

    @Query("SELECT * from challenges")
    fun getChallengesStream(): Flow<List<LocalChallengeEntity>>

    @Query("DELETE from challenges")
    suspend fun clearAll()

    @Query("""UPDATE challenges SET date = NULL, progress = 0
    """)
    suspend fun resetChallenges()

    @Query("SELECT * from challenges WHERE date IS NOT NUll")
    fun getActiveChallengesStream(): Flow<List<LocalChallengeEntity>>

    @Query(
        """SELECT * from challenges
ORDER BY RANDOM()
LIMIT(:randomCount)"""
    )
    suspend fun getRandomChallengeSelection(randomCount: String): List<LocalChallengeEntity>

}