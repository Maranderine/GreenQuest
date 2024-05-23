package de.hsb.greenquest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.hsb.greenquest.data.AppDataContainer
import de.hsb.greenquest.data.ChallengeRepository
import de.hsb.greenquest.data.LocalChallenge
import de.hsb.greenquest.data.toExternal
import de.hsb.greenquest.data.toLocal
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Challenge as ok_Challenge
import de.hsb.greenquest.Challenge as Challenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChallengeViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {
//class ChallengeViewModel() : ViewModel() {

    val formater = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val today = java.time.LocalDateTime.now().format(formater);

    private var _challengeList: MutableStateFlow<List<LocalChallenge>> = MutableStateFlow(listOf<LocalChallenge>())

    val challengeList: StateFlow<List<LocalChallenge>> = _challengeList.asStateFlow()


    init {
        viewModelScope.launch {
            challengeRepository.getActiveChallengesStream().transform {
                val challenges = it.toExternal()
                if (challenges.all { value -> value.done }) {
                    emit(listOf<LocalChallenge>())
                } else {
                    emit(challenges.toLocal())
                }
            }.collect{l -> _challengeList.value = l}
        }
    }
    suspend fun insert(){
        val challenge: de.hsb.greenquest.Challenge = Challenge(0, "this is a test", "rose",7, 3, "date")
        challengeRepository.insertChallenge(challenge)
    }

    suspend fun delete(challenge: Challenge){
        challengeRepository.deleteChallenge(challenge.toLocal())
    }


    suspend fun resetChallenges(){
        challengeRepository.resetChallenges()
    }

    suspend fun updateChallenge(challenge: Challenge){
        challengeRepository.updateChallenge(challenge.toLocal())
    }

    suspend fun refreshChallenges(){
        this.resetChallenges()
        val newChallengeSelection = challengeRepository.getRandom(4)
        newChallengeSelection.forEach{challenge -> challengeRepository.updateChallenge(challenge.copy(date = today))}
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
