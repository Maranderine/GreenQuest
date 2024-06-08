package de.hsb.greenquest.data.repository

import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.data.local.dao.ChallengeDao
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChallengeRepositoryImpl@Inject constructor(
    private val challengeDao: ChallengeDao
): ChallengeRepository {
    override fun getAllChallengesStream(): Flow<List<LocalChallengeEntity>> {
        return challengeDao.getChallengesStream()
    }

    override fun getActiveChallenges(): List<LocalChallengeEntity> {
        return challengeDao.getActiveChallenges()
    }

    override fun getActiveChallengesStream(): Flow<List<LocalChallengeEntity>> {
        return challengeDao.getActiveChallengesStream()
    }

    override suspend fun insertChallenge(challenge: Challenge) {
        challengeDao.insert(challenge.toLocal())
    }

    override suspend fun deleteChallenge(challenge: LocalChallengeEntity) {
        challengeDao.delete(challenge)
    }

    override suspend fun updateChallenge(challenge: LocalChallengeEntity) {
        challengeDao.update(challenge)
    }

    override suspend fun clearAll() {
        challengeDao.clearAll()
    }

    override suspend fun resetChallenges() {
        challengeDao.resetChallenges()
    }

    override suspend fun getRandom(randomCount: Number): List<LocalChallengeEntity> {
        return challengeDao.getRandomChallengeSelection(randomCount.toString())
    }
}