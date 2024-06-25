package de.hsb.greenquest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import de.hsb.greenquest.data.repository.AchievementsRepositoryImpl
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.domain.repository.AchievementsRepository
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
    private val achievementsRepository: AchievementsRepository
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

    private var _points: MutableStateFlow<Int> = MutableStateFlow(0)

    val points: StateFlow<Int?> = _points.asStateFlow()

    init {
        viewModelScope.launch {
           dailyChallengeRepository.getActiveChallengesStream().transform {
                if (it.all { value -> val t = today; value.date == today }) {

                    emit(it)
                } else {
                    emit(emptyList<DailyChallenge>())

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
        viewModelScope.launch {
            achievementsRepository.points.collect{
                _points.value = it
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

