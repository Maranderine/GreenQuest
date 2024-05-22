package de.hsb.greenquest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.hsb.greenquest.data.AppDataContainer
import de.hsb.greenquest.data.ChallengeRepository
import de.hsb.greenquest.data.LocalChallenge
import de.hsb.greenquest.data.toExternal
import de.hsb.greenquest.data.toLocal
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Challenge as ok_Challenge
import de.hsb.greenquest.Challenge as Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

class ChallengeViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {
//class ChallengeViewModel() : ViewModel() {
    var challengesUiState by mutableStateOf(ChallengesUiState())
        private set

    var challengeList: Flow<List<LocalChallenge>> = challengeRepository.getActiveChallengesStream().transform {
        val challenges = it.toExternal()
        if (challenges.all{ value -> value.done  }){
            emit(listOf<LocalChallenge>())
        }
        else{
            emit(challenges.toLocal())
        }
    }

    init {
        // emit(it.toExternal())
            /*val challenges = it.toExternal()
            if (challenges.all{ it.done }){
                emit(listOf<Challenge>())
            }
            else{
                emit(challenges)
            }*/


    }
    suspend fun insert(){
        val challenge: de.hsb.greenquest.Challenge = Challenge(0, "this is a test", "rose",7, 3, "date")
        challengeRepository.insertChallenge(challenge)
    }

    suspend fun delete(challenge: Challenge){
        challengeRepository.deleteChallenge(challenge.toLocal())
    }

    fun updateUiState(list: List<Challenge>){
        challengesUiState = ChallengesUiState(list)

    }

    suspend fun resetChallenges(){
        challengeRepository.resetChallenges()
    }

    suspend fun updateChallenge(challenge: Challenge){
        challengeRepository.updateChallenge(challenge.toLocal())
    }

    fun updateUiState(challenge: Challenge, index: Int){
        val currentChallenges = challengesUiState.challenges
        val newList = currentChallenges.slice(0 until index) + challenge //+ currentChallenges.slice(index+1 until currentChallenges.size)
        challengesUiState = ChallengesUiState(newList)
    }

    suspend fun clearAll(){
        challengeRepository.clearAll()
    }

    suspend fun refreshChallenges(){
        this.resetChallenges()
        val newChallengeSelection = challengeRepository.getRandom(4)
        newChallengeSelection.forEach{challenge -> challengeRepository.updateChallenge(challenge.copy(date = "today"))}
    }

}

data class ChallengesUiState(
    var challenges: List<de.hsb.greenquest.Challenge> = listOf<Challenge>()
)

data class Challenge(
    val id: Int = 0,
    val description: String,
    val Plant: String,
    val requiredCount: Int,
    val progress: Int,
    val date: String?
) {
    val done: Boolean
        get() = this.progress >= this.requiredCount
}
