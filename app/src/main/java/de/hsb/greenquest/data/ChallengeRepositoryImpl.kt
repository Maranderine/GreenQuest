package de.hsb.greenquest.data

import de.hsb.greenquest.Challenge
import kotlinx.coroutines.flow.Flow

class ChallengeRepositoryImpl(private val challengeDao: ChallengeDao): ChallengeRepository {
    override fun getAllChallengesStream(): Flow<List<LocalChallenge>> {
        return challengeDao.getChallengesStream()
    }

    override fun getActiveChallengesStream(): Flow<List<LocalChallenge>> {
        return challengeDao.getActiveChallengesStream()
    }

    override suspend fun insertChallenge(challenge: Challenge) {
        challengeDao.insert(challenge.toLocal())
    }

    override suspend fun deleteChallenge(challenge: LocalChallenge) {
        challengeDao.delete(challenge)
    }

    override suspend fun updateChallenge(challenge: LocalChallenge) {
        challengeDao.update(challenge)
    }

    override suspend fun clearAll() {
        challengeDao.clearAll()
    }

    override suspend fun resetChallenges() {
        challengeDao.resetChallenges()
    }

    override suspend fun getRandom(randomCount: Number): List<LocalChallenge> {
        return challengeDao.getRandomChallengeSelection(randomCount.toString())
    }
}