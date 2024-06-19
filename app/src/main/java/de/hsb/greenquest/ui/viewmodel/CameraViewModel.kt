package de.hsb.greenquest.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.ChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import de.hsb.greenquest.domain.usecase.EventManager
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(

    private val takePictureUseCase: TakePictureUseCase,
    private val plantNetRepository: PlantNetRepository,
    private val challengeRepository: ChallengeRepository,
): ViewModel() {
//    var imagePath by mutableStateOf<String>("")
//    var imageName by mutableStateOf<String>("")

    //var plantFileName by mutableStateOf<String>("")

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    private val _navigateToNextScreen = MutableStateFlow(false)
    val navigateToNextScreen = _navigateToNextScreen.asStateFlow()

    fun savePicture(plantFileName: String, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                takePictureUseCase.takePicture(plantFileName, imagePath)
                _errorState.value = null
                _navigateToNextScreen.value = true
            } catch (e: TakePictureUseCase.PlantIdentificationException) {
                _errorState.value = e.message
                _navigateToNextScreen.value = false // Prevent navigation
            }
        }
    }

    fun errorProcessed() {
        // Reset UI (error) state flow
        _errorState.value = null
        _navigateToNextScreen.value = false
    }

    fun identify(imagePath: String){
        viewModelScope.launch(Dispatchers.IO) {
           ( plantNetRepository.identifyPlant(imagePath))?.let { plant ->
               val activeChallenges = challengeRepository.getActiveChallenges()
               activeChallenges.forEach{
                   println("compare ${it.Plant} == ${plant.name}")
                   if(it.Plant == plant.name){challengeRepository.updateChallenge(it.copy(progress = it.progress+1))}
               }
           }
        }
    }
}