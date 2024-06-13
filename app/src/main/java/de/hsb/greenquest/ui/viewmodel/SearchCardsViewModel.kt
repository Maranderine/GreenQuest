package de.hsb.greenquest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.ChallengeCardRepository
import de.hsb.greenquest.domain.model.challengeCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCardsViewModel @Inject constructor(
    private val challengeCardRepository: ChallengeCardRepository?
): ViewModel()  {

    private var _challengeCards: MutableStateFlow<List<challengeCard>> = MutableStateFlow(listOf<challengeCard>())

    val challengeCards: StateFlow<List<challengeCard>> = _challengeCards.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _cardsIdx: MutableStateFlow<Int> = MutableStateFlow(0)

    val cardsIdx: StateFlow<Int> = _cardsIdx.asStateFlow()

    init{
        _loading.value = true
        viewModelScope.launch {

            print("IN LAUNCH")
            challengeCardRepository?.getChallengeCardByIndex(0)?.let {
                _challengeCards.value += listOf(it)
           }
            print("CHALLENGE CARDS: " + _challengeCards.value.toString())
            _loading.value = false
        }
    }

    fun changeIdx(direction: Int){
        var dir = direction
        if(_cardsIdx.value + dir < 0 || _cardsIdx.value + dir > challengeCards.value.size){
            dir = 0
        }
        this._cardsIdx.value += dir
    }

}