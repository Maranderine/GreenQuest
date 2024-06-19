package de.hsb.greenquest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel@Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
) : ViewModel() {
//class ChallengeViewModel() : ViewModel() {

    val formater = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val today = java.time.LocalDateTime.now().format(formater);

    private var _challengeList: MutableStateFlow<List<DailyChallenge>> = MutableStateFlow(listOf<DailyChallenge>())

    val challengeList: StateFlow<List<DailyChallenge>> = _challengeList.asStateFlow()

    private var _progress: MutableStateFlow<Int?> = MutableStateFlow(null)

    val progress: StateFlow<Int?> = _progress.asStateFlow()

    private var _requiredCount: MutableStateFlow<Int?> = MutableStateFlow(null)

    val requiredCount: StateFlow<Int?> = _requiredCount.asStateFlow()

    init {
        print("IN INIT FUNCTION OF CHALLENG")
        viewModelScope.launch {
           dailyChallengeRepository.getActiveChallengesStream().transform {
                if (it.all { value -> val t = today; Log.d("help;-;", "todays date is xxx $t"); value.date === today }) {
                    //emit(challenges.toLocal())
                    emit(emptyList<DailyChallenge>())

                } else {
                    emit(it)

                    //emit(emptyList<LocalChallengeEntity>())
                }
            }.collect{
                challenges -> _challengeList.value = challenges
                if(challenges.isNotEmpty()){
                    _progress.value = challenges.sumOf { c -> c.progress }
                    _requiredCount.value = challenges.sumOf { c -> c.requiredCount }
                }else{
                    _progress.value = 1
                    _requiredCount.value = -1
                }
            }
        }
    }

    suspend fun delete(challenge: DailyChallenge){
        dailyChallengeRepository.deleteActiveChallenge(challenge)
    }

    suspend fun clearChallenges(){
        dailyChallengeRepository.clearAllActiveChallenges()
    }


    suspend fun updateChallenge(challenge: DailyChallenge){
        dailyChallengeRepository.updateActiveChallenge(challenge)
    }

    suspend fun refreshChallenges(){
        clearChallenges()
        val newChallengeSelection = dailyChallengeRepository.getNewRandomlyPickedListOfActiveChallenges(4)
        newChallengeSelection.forEach{challenge -> dailyChallengeRepository.insertChallengeIntoActiveChallenges(challenge)}
    }
}

