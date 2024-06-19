package de.hsb.greenquest.data.repository

import android.content.Context
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject


class AchievementsRepositoryImpl @Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeCardRepository: ChallengeCardRepository,
    private val applicationContext: Context
) {
    private val activeDailyChallenges: List<DailyChallenge> = emptyList()
    private val _points: MutableStateFlow<Int> = MutableStateFlow(0)
    private val points: StateFlow<Int> = _points.asStateFlow()
    private val USER_POINTS_FILE_PATH = "userPoints.txt"

    init {
        _points.value = getUserPoints()?: 0
    }
    fun writeTo(filename: String, fileContents: String){

        applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun readFrom(filename: String): String{
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

    fun getUserPoints(): Int?{
        return try {
            Integer.parseInt(readFrom(USER_POINTS_FILE_PATH))
        }catch (e: Exception){
            null
        }
    }

    fun saveUserPoints(){
        writeTo(USER_POINTS_FILE_PATH, _points.value.toString())
    }

    suspend fun checkChallenges(plant: Plant){
        val activeDailyChallenges = dailyChallengeRepository.getActiveChallenges()
        val activeChallengeCards = challengeCardRepository.getAvailableChallengeCardsData()

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