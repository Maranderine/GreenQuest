package de.hsb.greenquest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(challenge: LocalChallenge)

    @Update
    suspend fun update(challenge: LocalChallenge)

    @Delete
    suspend fun delete(challenge: LocalChallenge)

    @Query("SELECT * from challenges")
    fun getChallengesStream(): Flow<List<LocalChallenge>>

    @Query("DELETE from challenges")
    suspend fun clearAll()

    @Query("""UPDATE challenges SET date = NULL, progress = 0
    """)
    suspend fun resetChallenges()

    @Query("SELECT * from challenges WHERE date IS NOT NUll")
    fun getActiveChallengesStream(): Flow<List<LocalChallenge>>

    @Query(
        """SELECT * from challenges
ORDER BY RANDOM()
LIMIT(:randomCount)"""
    )
    suspend fun getRandomChallengeSelection(randomCount: String): List<LocalChallenge>

}