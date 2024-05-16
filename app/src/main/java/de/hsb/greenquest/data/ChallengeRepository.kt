package de.hsb.greenquest.data

import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    /**
     * Retrieve all the Challenges from the the given data source.
     */
    fun getAllChallengesStream(): Flow<List<LocalChallenge>>


    /**
     * Insert item in the data source
     */
    suspend fun insertChallenge(challenge: LocalChallenge)

    /**
     * Delete item from the data source
     */
    suspend fun deleteChallenge(challenge: LocalChallenge)

    /**
     * Update item in the data source
     */
    suspend fun updateChallenge(challenge: LocalChallenge)
}