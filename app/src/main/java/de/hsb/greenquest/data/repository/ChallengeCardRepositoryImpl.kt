package de.hsb.greenquest.data.repository

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import de.hsb.greenquest.data.local.ChallengeCardImageInternalStorageLoader
import de.hsb.greenquest.data.local.dao.ChallengeCardDao
import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import de.hsb.greenquest.data.network.ChallengeCardDataSource
import de.hsb.greenquest.data.network.ChallengeCardPicturesDataSource
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.model.challengeCard
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ChallengeCardRepositoryImpl @Inject constructor(
    private val firebaseApp: FirebaseApp?,
    private val challengeCardDao: ChallengeCardDao, //for personal active challenge
    private val challengeCardImageLoader: ChallengeCardImageInternalStorageLoader, //for respective images
    private val challengeCardDataSource: ChallengeCardDataSource, // for online available challenge cards
    private val challengeCardPicturesDataSource: ChallengeCardPicturesDataSource // for respective pictures
): ChallengeCardRepository {

    private val db = Firebase.firestore

    override suspend fun loadNewChallengeCard(): challengeCard?{
        //download online data
        val cardsData = challengeCardDataSource.getChallengeCards()
        if(cardsData.isNotEmpty()){
            val currentCardsIds = challengeCardDao.getChallengeCardsData().map { it.id }
            var cardDoc = cardsData.shuffled().find { it.id !in currentCardsIds }
            if(cardDoc != null){
                val cardData = cardDoc.data
                val cardId = cardDoc.id

                //dowload online image
                val img =
                    challengeCardPicturesDataSource.downloadImageFromStorage(cardData!!["imagePath"].toString())
                return img?.let {
                    //save image locally
                    val path = challengeCardImageLoader.saveToInternalStorage(img)
                    val challenge: ChallengeCardEntity = ChallengeCardEntity(
                        id = cardId,
                        name = cardData["name"].toString(),
                        imagePath = path,
                        location = cardData["location"].toString(),
                        hint = cardData["hint"].toString()
                    )
                    //save new created card locally
                    //challengeCardDao.insert(challenge)
                    challengeCardDao.insertInto(
                        challenge.id,
                        challenge.name,
                        challenge.imagePath,
                        challenge.location,
                        challenge.hint
                    )
                    return challenge.toChallengeCard()
                }
            } else{
                throw NoNewChallengesException("you have all available challenges loaded")
            }
        }
        return null
    }

    override suspend fun removeChallengeFromActive(challenge: challengeCard) {
        challengeCardDao.delete(challenge.id)
    }

    override suspend fun createNewChallengeCard(plant: Plant, imagePath: String, location: String, hint: String){
        val imgPathInStorage = challengeCardPicturesDataSource.uploadImageToStorage(imagePath)
        challengeCardDataSource.saveChallengeCardData(imagePath = imgPathInStorage, name = plant.name, hint = hint, location = location)
    }

    override fun mapEntitiesToModel(challengeCardEntity: ChallengeCardEntity, imgPath: String): challengeCard{
        return challengeCard(
            id = challengeCardEntity.id,
            imgPath = imgPath,
            name = challengeCardEntity.name,
            location = challengeCardEntity.location,
            hint = challengeCardEntity.hint
        )
    }

    override fun getActiveChallengeCards(): Flow<List<challengeCard>> {
        return challengeCardDao.getChallengeCardsDataStream().transform {
            emit(it.map { c -> c.toChallengeCard() })
        }
    }

    override suspend fun getAvailableChallengeCardsData(): List<ChallengeCardEntity>{
        return challengeCardDao.getChallengeCardsData()
    }
}

class NoNewChallengesException(message: String) : Exception(message)