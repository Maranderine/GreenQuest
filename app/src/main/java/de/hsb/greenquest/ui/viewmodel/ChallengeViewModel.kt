package de.hsb.greenquest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.domain.repository.ChallengeRepository
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.data.repository.toExternal
import de.hsb.greenquest.data.repository.toLocal
import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.domain.usecase.EventManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel@Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val eventManager: EventManager
) : ViewModel() {
//class ChallengeViewModel() : ViewModel() {

    val formater = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val today = java.time.LocalDateTime.now().format(formater);

    private var _challengeList: MutableStateFlow<List<LocalChallengeEntity>> = MutableStateFlow(listOf<LocalChallengeEntity>())

    val challengeList: StateFlow<List<LocalChallengeEntity>> = _challengeList.asStateFlow()

    init {
        viewModelScope.launch {
            challengeRepository.getActiveChallengesStream().transform {
                val challenges = it.toExternal()
                if (challenges.all { value -> value.done }) {
                    emit(listOf<LocalChallengeEntity>())
                } else {
                    emit(challenges.toLocal())
                }
            }.collect{l -> _challengeList.value = l}
        }
    }
    suspend fun insert(){
        val challenge: Challenge = Challenge(0, "this is a test to see how a very long description would look like as a card", "this is a test to see how a very long description would look like as a card",7, 3, "date")
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
    var challenges: List<Challenge> = listOf<Challenge>()
)

