package de.hsb.greenquest.ui.viewmodel

<<<<<<< HEAD
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val takePictureUseCase: TakePictureUseCase
): ViewModel() {
//    var imagePath by mutableStateOf<String>("")
//    var imageName by mutableStateOf<String>("")

    fun savePicture(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            takePictureUseCase.takePicture(imagePath = imagePath)
        }
    }
}