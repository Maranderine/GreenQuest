package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import de.hsb.greenquest.data.local.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {

    @Query("SELECT * from dailyChallenges")
    fun getChallengesStream(): Flow<List<DailyChallengeEntity>>

    @Query("SELECT * from dailyChallenges")
    suspend fun getChallenges(): List<DailyChallengeEntity>

    @Query("SELECT * from dailyChallenges WHERE id = :id")
    suspend fun getChallengeById(id: Int): List<DailyChallengeEntity>

    @Query(
        """SELECT * from dailyChallenges
ORDER BY RANDOM()
LIMIT(:randomCount)"""
    )
    suspend fun getRandomChallengeSelection(randomCount: String): List<DailyChallengeEntity>

}