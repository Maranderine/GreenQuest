package de.hsb.greenquest.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.AchievementsRepositoryImpl
import de.hsb.greenquest.data.repository.ChallengeCardRepositoryImpl
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import de.hsb.greenquest.domain.usecase.PlantIdentificationException
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*
import de.hsb.greenquest.data.local.mediastore.PlantPictureMediaStoreLoader
import de.hsb.greenquest.domain.repository.AchievementsRepository
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.File


//import androidx.compose.runtime.livedata.observeAsState


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val achievementsRepository: AchievementsRepository,
    private val takePictureUseCase: TakePictureUseCase,
    private val plantNetRepository: PlantNetRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeCardRepository: ChallengeCardRepository,
    private val plantPictureMediaStoreLoader: PlantPictureMediaStoreLoader
): ViewModel() {

    var plant by mutableStateOf<Plant?>(null)
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    val _shouldNavigate = MutableStateFlow(false)

    /**
     * saves plant picture and navigates to the the associated portfolio entry
     */
    fun savePicture(plantFileName: String, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                takePictureUseCase.takePicture(plantFileName, imagePath)
                _shouldNavigate.value = true
            } catch (e: PlantIdentificationException) {
                // change of error value triggers Toast display

                _error.value = e.message
            }
        }
    }

    /**
     * deletes image
     * @param String file path of image
     */
    fun deleteImage(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val deleted = imageFile.delete()
            if (deleted) {
                println("Image deleted successfully.")
            } else {
                println("Failed to delete the image.")
            }
        } else {
            println("Image file not found.")
        }
    }

    fun resetError() {
        _error.value = null
    }

    /**
     * identifies given plant image
     * @param String path to plant image file
     */
    fun identify(imagePath: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ( plantNetRepository.identifyPlant(imagePath))?.let { p ->
                    achievementsRepository.checkChallenges(p)
                    plant = p
                }
            } catch (e: Exception) {
                _error.value = "No Plant Recognized. Pleas Try Again"
                deleteImage(imagePath)
            }
        }
    }

    /**
     * creates challenge card from a given plant image
     * @param String path to image file
     * @param String? optional, hint that can be added to the challenge card to help find the displayed plant
     */
    fun createChallengeCard(imagePath: String, hint: String = ""){
        viewModelScope.launch(Dispatchers.IO) {
            plantNetRepository.identifyPlant(imagePath)?.apply {
                print(this.toString())
                challengeCardRepository?.createNewChallengeCard(this, imagePath, hint = hint)
            }
        }
    }
}