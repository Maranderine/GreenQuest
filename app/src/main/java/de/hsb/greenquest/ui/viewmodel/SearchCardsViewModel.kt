package de.hsb.greenquest.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.ChallengeCardRepositoryImpl
import de.hsb.greenquest.domain.model.challengeCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCardsViewModel @Inject constructor(
    private val challengeCardRepositoryImpl: ChallengeCardRepositoryImpl?
): ViewModel()  {

    private var _challengeCards: MutableStateFlow<List<challengeCard>> = MutableStateFlow(listOf<challengeCard>())

    val challengeCards: StateFlow<List<challengeCard>> = _challengeCards.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _cardsIdx: MutableStateFlow<Int> = MutableStateFlow(0)

    val cardsIdx: StateFlow<Int> = _cardsIdx.asStateFlow()

    private val _openDialog = MutableStateFlow(false)

    val openDialog: StateFlow<Boolean> = _openDialog.asStateFlow()

    private val _DialogText = MutableStateFlow("no hint was given")

    val DialogText: StateFlow<String> = _DialogText.asStateFlow()



    init{
        _loading.value = true
        viewModelScope.launch {
            challengeCardRepositoryImpl?.getActiveChallengeCards()?.collect{
                _challengeCards.value = it
                _loading.value = false
            }

            openDialog.collect{
                if(!it){
                    _DialogText.value = "no hint was given"
                }
            }
        }
    }

    fun changeIdx(value: Int){
        _cardsIdx.value = when(value){
            in Int.MIN_VALUE..0 -> 0
            in 0.._challengeCards.value.size -> value
            else -> _challengeCards.value.size
        }
    }

    fun toggleDialog(){
        _DialogText.value = "no hint was given"
        _openDialog.value = !_openDialog.value
    }

    fun setDialogText(text: String){
        _DialogText.value = text
    }

    fun deleteCard(){
        _loading.value = true
        viewModelScope.launch {
            challengeCardRepositoryImpl?.removeChallengeFromActive(_challengeCards.value[_cardsIdx.value])
            _cardsIdx.value = if(cardsIdx.value > _challengeCards.value.size) (_cardsIdx.value -1) else _cardsIdx.value
        }
    }
    fun loadChallengeCard(){
        _loading.value = true
        viewModelScope.launch {
            challengeCardRepositoryImpl?.loadNewChallengeCard()
        }
    }
}
//TODO threshhold plant net