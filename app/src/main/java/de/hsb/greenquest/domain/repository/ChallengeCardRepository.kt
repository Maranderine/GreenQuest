package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import de.hsb.greenquest.data.repository.toChallengeCard
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.model.challengeCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

interface ChallengeCardRepository {
    suspend fun loadNewChallengeCard(): challengeCard?

    suspend fun removeChallengeFromActive(challenge: challengeCard)

    suspend fun createNewChallengeCard(plant: Plant, imagePath: String, location: String = "", hint: String = "")

    fun mapEntitiesToModel(challengeCardEntity: ChallengeCardEntity, imgPath: String): challengeCard

    fun getActiveChallengeCards(): Flow<List<challengeCard>>

    suspend fun getAvailableChallengeCardsData(): List<ChallengeCardEntity>

}