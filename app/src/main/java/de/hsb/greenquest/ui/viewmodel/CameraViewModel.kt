package de.hsb.greenquest.ui.viewmodel

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

    fun savePicture(plantFileName: String, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            takePictureUseCase.takePicture(plantFileName, imagePath)
        }
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