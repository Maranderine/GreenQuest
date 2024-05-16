package de.hsb.greenquest.data

import kotlinx.coroutines.flow.Flow

class ChallengeRepositoryImpl(private val challengeDao: ChallengeDao): ChallengeRepository {
    override fun getAllChallengesStream(): Flow<List<LocalChallenge>> {
        return challengeDao.getChallenges()
    }

    override suspend fun insertChallenge(challenge: LocalChallenge) {
        challengeDao.insert(challenge)
    }

    override suspend fun deleteChallenge(challenge: LocalChallenge) {
        challengeDao.delete(challenge)
    }

    override suspend fun updateChallenge(challenge: LocalChallenge) {
        challengeDao.update(challenge)
    }
}