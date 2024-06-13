package de.hsb.greenquest.ui.viewmodel

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.domain.repository.ChallengeRepository
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.data.repository.ChallengeCardRepository
import de.hsb.greenquest.data.repository.toExternal
import de.hsb.greenquest.data.repository.toLocal
import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.domain.model.challengeCard
import de.hsb.greenquest.domain.usecase.EventManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel@Inject constructor(
    private val challengeRepository: ChallengeRepository,
    //private val challengeCardRepository: ChallengeCardRepository?
) : ViewModel() {
//class ChallengeViewModel() : ViewModel() {

    val formater = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val today = java.time.LocalDateTime.now().format(formater);

    private var _challengeList: MutableStateFlow<List<LocalChallengeEntity>> = MutableStateFlow(listOf<LocalChallengeEntity>())

    val challengeList: StateFlow<List<LocalChallengeEntity>> = _challengeList.asStateFlow()

    private var _progress: MutableStateFlow<Int> = MutableStateFlow(1)

    val progress: StateFlow<Int> = _progress.asStateFlow()

    private var _requiredCount: MutableStateFlow<Int> = MutableStateFlow(-1)

    val requiredCount: StateFlow<Int> = _requiredCount.asStateFlow()

    init {
        print("IN INIT FUNCTION OF CHALLENG")
        /*viewModelScope.launch {
            val challenges = challengeRepository.getActiveChallengesStream()
            print("INSIDE CHALLENGE VIEWMODEL" + challenges)

            challenges.map {
                it.toExternal();
                it.sumOf { value -> value.progress }
            }.collect{p -> _progress.value = p}

            challenges.map {
                it.toExternal();
                it.sumOf { value -> value.requiredCount }
            }.collect{r -> print("INSIDE CHALLENGE VIEWMODEL" + r); _requiredCount.value = r}

            //returns only challenges if from today
            challenges.transform {
                emit(it)
                val challenges = it.toExternal()
                if (challenges.all { value -> value.date === today }) {
                    emit(challenges.toLocal())
                } else {
                    emit(challenges.toLocal())
                }
            }.collect{l -> _challengeList.value = l}
        }*/
        viewModelScope.launch {

            challengeRepository.getActiveChallengesStream().transform {
                val challenges = it.toExternal()
                if (challenges.all { value -> val t = today; Log.d("help;-;", "todays date is xxx $t"); value.date === today }) {
                    //emit(challenges.toLocal())
                    emit(emptyList<LocalChallengeEntity>())

                } else {
                    emit(challenges.toLocal())

                    //emit(emptyList<LocalChallengeEntity>())
                }
            }.collect{
                l -> _challengeList.value = l
                if(l.size > 0){
                    _progress.value = l.sumOf { c -> c.progress }
                    _requiredCount.value = l.sumOf { c -> c.requiredCount }
                }else{
                    _progress.value = 1
                    _requiredCount.value = -1
                }
            }
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
        print("INSIDER REFRESH FUNCTION")
        newChallengeSelection.forEach{challenge -> challengeRepository.updateChallenge(challenge.copy(date = today))}
    }

}

data class ChallengesUiState(
    var challenges: List<Challenge> = listOf<Challenge>()
)

