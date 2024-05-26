package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    /**
     * Retrieve all the Challenges from the the given data source.
     */
    fun getAllChallengesStream(): Flow<List<LocalChallengeEntity>>

    fun getActiveChallengesStream(): Flow<List<LocalChallengeEntity>>

    /**
     * Insert item in the data source
     */
    suspend fun insertChallenge(challenge: Challenge)

    /**
     * Delete item from the data source
     */
    suspend fun deleteChallenge(challenge: LocalChallengeEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateChallenge(challenge: LocalChallengeEntity)

    suspend fun clearAll()

    suspend fun resetChallenges()

    suspend fun getRandom(randomCount: Number): List<LocalChallengeEntity>


}