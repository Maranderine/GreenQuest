package de.hsb.greenquest.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import de.hsb.greenquest.data.local.dao.ActiveDailyChallengeDao
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.data.local.dao.DailyChallengeDao
import de.hsb.greenquest.data.local.entity.ActiveDailyChallengeEntity
import de.hsb.greenquest.data.local.entity.DailyChallengeEntity
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class DailyChallengeRepositoryImpl@Inject constructor(
    private val dailyChallengeDao: DailyChallengeDao,
    private val activeDailyChallengeDao: ActiveDailyChallengeDao
): DailyChallengeRepository {

    /**
     * gets List of all available Challenges from Challenge Database
     */
    private suspend fun getAllAvailableChallenges(): List<DailyChallengeEntity> {
        return dailyChallengeDao.getChallenges()
    }

    /**
     * inserts item into activeChallenges database
     */
    override suspend fun insertChallengeIntoActiveChallenges(challenge: DailyChallenge) {
        activeDailyChallengeDao.insert(challenge.toActive())
    }

    /**
     * Delete item from activeChallenges database
     */
    override suspend fun deleteActiveChallenge(challenge: DailyChallenge) {
        activeDailyChallengeDao.delete(challenge.toActive())
    }

    /**
     * updates item from activeChallenges database
     */
    override suspend fun updateActiveChallenge(challenge: DailyChallenge) {
        activeDailyChallengeDao.update(challenge.toActive())
    }

    /**
     * Deletes all items from activeChallenges Database
     */
    //TODO clear not working?
    override suspend fun clearAllActiveChallenges() {
        try {
            activeDailyChallengeDao.clearAll()
        }catch (err: Exception){
            Log.d(TAG, err.toString())
        }
    }

    /**
     * gets a list of a given size from the challenges database
     * sets them active by inserting them into the activeChallenges database
     * returns List as List of Challenges
     */
    override suspend fun getNewRandomlyPickedListOfActiveChallenges(randomCount: Number): List<DailyChallenge> {
        return dailyChallengeDao.getRandomChallengeSelection(randomCount.toString()).toChallenge()
    }

    /**
     * returns Stream of currently active Challenges
     */
    override fun getActiveChallengesStream(): Flow<List<DailyChallenge>> {
        return activeDailyChallengeDao.getChallengesStream().transform {
            emit(it.map { c -> mapActiveChallengeEntitytoChallengeModel(c) })
        }
    }

    override suspend fun getActiveChallenges(): List<DailyChallenge> {
        return activeDailyChallengeDao.getChallenges().map { c -> mapActiveChallengeEntitytoChallengeModel(c) }
    }

    /**
     * returns challenge from challenges database based on a given id
     */
    private suspend fun getChallengeById(id: Int): DailyChallengeEntity?{
        if(dailyChallengeDao.getChallengeById(id).isNotEmpty()){
            return dailyChallengeDao.getChallengeById(id)[0]
        }else{
            return null
        }
    }

    /**
     * maps active Challenge Entity to Challenge Model
     * loads respective challenge Data from challenges database based on id
     * returns mapped Challenge object
     */
    private suspend fun mapActiveChallengeEntitytoChallengeModel(activeChallenge:ActiveDailyChallengeEntity): DailyChallenge{
        val challengeData = getChallengeById(activeChallenge.challengeId)
        return DailyChallenge(
            challengeId = activeChallenge.challengeId,
            description = challengeData!!.description,
            type = challengeData.type,
            date = activeChallenge.date,
            progress = activeChallenge.progress,
            requiredCount = challengeData.requiredCount
        )
    }
}