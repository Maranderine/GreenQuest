package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.hsb.greenquest.data.local.entity.ActiveDailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveDailyChallengeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(challenge: ActiveDailyChallengeEntity)

    @Update
    suspend fun update(challenge: ActiveDailyChallengeEntity)

    @Delete
    suspend fun delete(challenge: ActiveDailyChallengeEntity)

    @Query("DELETE from activeDailyChallenges")
    suspend fun clearAll()

    @Query("SELECT * from activeDailyChallenges")
    fun getChallengesStream(): Flow<List<ActiveDailyChallengeEntity>>

    @Query("SELECT * from activeDailyChallenges")
    fun getChallenges(): List<ActiveDailyChallengeEntity>

}