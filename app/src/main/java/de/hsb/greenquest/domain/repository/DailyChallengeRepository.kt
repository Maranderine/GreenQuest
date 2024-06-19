package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.domain.model.DailyChallenge
import kotlinx.coroutines.flow.Flow

interface DailyChallengeRepository {

    suspend fun insertChallengeIntoActiveChallenges(challenge: DailyChallenge)

    suspend fun deleteActiveChallenge(challenge: DailyChallenge)

    suspend fun updateActiveChallenge(challenge: DailyChallenge)

    suspend fun getNewRandomlyPickedListOfActiveChallenges(randomCount: Number): List<DailyChallenge>

    suspend fun clearAllActiveChallenges()

    suspend fun getActiveChallenges(): List<DailyChallenge>

    fun getActiveChallengesStream(): Flow<List<DailyChallenge>>
}

//TODO write repository that saves images as well as data of challenge cards to internal storage