package de.hsb.greenquest.data.repository

import android.content.Context
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.AchievementsRepository
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * responsible for handling the score/ points od the user
 * points are collected by finishing challenges
 * depends on challengeCardRepository and DailyChallengeRepository
 */
class AchievementsRepositoryImpl @Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeCardRepository: ChallengeCardRepository,
    private val applicationContext: Context
): AchievementsRepository{

    private val _points: MutableStateFlow<Int> = MutableStateFlow(0)
    override val points: StateFlow<Int> = _points.asStateFlow()
    private val USER_POINTS_FILE_PATH = "userPoints.txt"    // path where points are saved to

    init {
        _points.value = getUserPoints()?: 0
    }

    /**
     * general write to app specific internal storage
     */
    override fun writeTo(filename: String, fileContents: String){

        applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    /**
     * general read from from app specific internal storage
     */
    override fun readFrom(filename: String): String{
        val fis: FileInputStream = applicationContext.openFileInput(filename)
        val isr = InputStreamReader(fis)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        return sb.toString()

    }

    /**
     * get user points from app specific internal storage
     */
    override fun getUserPoints(): Int?{
        return try {
            Integer.parseInt(readFrom(USER_POINTS_FILE_PATH))
        }catch (e: Exception){
            null
        }
    }

    /**
     * saves user points to app specific internal storage
     */
    override fun saveUserPoints(){
        writeTo(USER_POINTS_FILE_PATH, _points.value.toString())
    }

    /**
     * checks wether a plant is required for an active challenge, either a daily challenge or a challenge card
     * if so increases user points and
     * deletes challenge card or
     * incress progress on daily challenge
     */
    override suspend fun checkChallenges(plant: Plant){
        val activeDailyChallenges = dailyChallengeRepository.getActiveChallenges()
        val activeChallengeCards = challengeCardRepository.getActiveChallengeCardsData()

        //val progress = activeDailyChallenges.all { it.done }
        activeDailyChallenges.forEach{
            if(it.type == plant.name && !it.done){
                dailyChallengeRepository.updateActiveChallenge(it.copy(progress = it.progress+1))
                _points.value += 10
            }
        }

        activeChallengeCards.forEach{
            if(it.name == plant.name){
                challengeCardRepository.removeChallengeFromActive(it.toChallengeCard())
                _points.value += 50
            }
        }

        saveUserPoints()
    }
    //check challenges
}