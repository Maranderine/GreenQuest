package de.hsb.greenquest.data.repository

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import de.hsb.greenquest.data.local.InternalStorage.ChallengeCardImageInternalStorageLoader
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

/**
 * class that handles complex buisness logic concerning the challengeCards
 */
class ChallengeCardRepositoryImpl @Inject constructor(
    private val firebaseApp: FirebaseApp?,
    private val challengeCardDao: ChallengeCardDao, //for personal active challenge
    private val challengeCardImageLoader: ChallengeCardImageInternalStorageLoader, //for respective images
    private val challengeCardDataSource: ChallengeCardDataSource, // for online available challenge cards
    private val challengeCardPicturesDataSource: ChallengeCardPicturesDataSource // for respective pictures
): ChallengeCardRepository {

    private val db = Firebase.firestore

    /**
     * loads a random new challenge Card
     */
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

    /**
     * removes card if challenge card was finished or deleted
     */
    override suspend fun removeChallengeFromActive(challenge: challengeCard) {
        challengeCardDao.delete(challenge.id)
    }

    /**
     * creates a new challenge card based on a plant
     * uploads the data to the Cloud firestore
     * uploads the image to the Cloud storage
     */
    override suspend fun createNewChallengeCard(plant: Plant, imagePath: String, location: String, hint: String){
        val imgPathInStorage = challengeCardPicturesDataSource.uploadImageToStorage(imagePath)
        challengeCardDataSource.saveChallengeCardData(imagePath = imgPathInStorage, name = plant.name, hint = hint, location = location)
    }

    /**
     * util function to quickly map card Entity to a card data model
     *
     * @param ChallengeCardEntity database entity of a locally saved, active challenge card
     * @param String path to the respective image
     * @return challenge Card data Model
     */
    override fun mapEntitiesToModel(challengeCardEntity: ChallengeCardEntity, imgPath: String): challengeCard{
        return challengeCard(
            id = challengeCardEntity.id,
            imgPath = imgPath,
            name = challengeCardEntity.name,
            location = challengeCardEntity.location,
            hint = challengeCardEntity.hint
        )
    }

    /**
     * returns all active challenge cards as a Stream
     */
    override fun getActiveChallengeCardsDataStream(): Flow<List<challengeCard>> {
        return challengeCardDao.getChallengeCardsDataStream().transform {
            emit(it.map { c -> c.toChallengeCard() })
        }
    }

    /**
     * returns all active challenge cards
     */
    override suspend fun getActiveChallengeCardsData(): List<ChallengeCardEntity>{
        return challengeCardDao.getChallengeCardsData()
    }
}

class NoNewChallengesException(message: String) : Exception(message)