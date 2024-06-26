package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.model.challengeCard
import kotlinx.coroutines.flow.Flow

interface ChallengeCardRepository {
    suspend fun loadNewChallengeCard(): challengeCard?

    suspend fun removeChallengeFromActive(challenge: challengeCard)

    suspend fun createNewChallengeCard(plant: Plant, imagePath: String, location: String = "", hint: String = "")

    fun mapEntitiesToModel(challengeCardEntity: ChallengeCardEntity, imgPath: String): challengeCard

    fun getActiveChallengeCardsDataStream(): Flow<List<challengeCard>>

    suspend fun getActiveChallengeCardsData(): List<ChallengeCardEntity>

}