package de.hsb.greenquest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.AchievementsRepositoryImpl
import de.hsb.greenquest.data.repository.ChallengeCardRepositoryImpl
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.*
import de.hsb.greenquest.domain.repository.AchievementsRepository
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import kotlinx.coroutines.flow.StateFlow

//import androidx.compose.runtime.livedata.observeAsState


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val achievementsRepository: AchievementsRepository,
    private val takePictureUseCase: TakePictureUseCase,
    private val plantNetRepository: PlantNetRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeCardRepository: ChallengeCardRepository
    //private val firebaseApp: FirebaseApp?,
): ViewModel() {

    var plant by mutableStateOf<Plant?>(null)

    fun savePicture(plantFileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            takePictureUseCase.takePicture(plantFileName)
        }
    }

    fun identify(imagePath: String){
        viewModelScope.launch(Dispatchers.IO) {
           ( plantNetRepository.identifyPlant(imagePath))?.let { p ->
               achievementsRepository.checkChallenges(p)
               plant = p
           }
        }
    }

    /*fun createChallengeCard(imagePath: String){
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()

        val imageName = "image.jpeg"


        val imageRef: StorageReference = storageRef.child("images/challengeCards/$imageName")

        val imageUri = Uri.fromFile(File(imagePath))

        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress: Double =
                100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()
        }.addOnSuccessListener { taskSnapshot ->
            // This listener is triggered when the file is uploaded successfully.
            // Using the below code you can get the download url of the file
            imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                val imageUrl: String = uri.toString()
            }
        }.addOnFailureListener { exception -> }
    }*/

    fun createChallengeCard(imagePath: String, hint: String?){
        viewModelScope.launch(Dispatchers.IO) {
            plantNetRepository.identifyPlant(imagePath)?.apply {
                print(this.toString())
                challengeCardRepository?.createNewChallengeCard(this, imagePath)
            }
        }
    }
}