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
    fun getChallenges(): Flow<List<LocalChallenge>>

}