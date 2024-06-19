package de.hsb.greenquest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.AchievementsRepositoryImpl
import de.hsb.greenquest.data.repository.ChallengeCardRepositoryImpl
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val achievementsRepositoryImpl: AchievementsRepositoryImpl,
    private val takePictureUseCase: TakePictureUseCase,
    private val plantNetRepository: PlantNetRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeCardRepositoryImpl: ChallengeCardRepositoryImpl
    //private val firebaseApp: FirebaseApp?,
): ViewModel() {

//    var imagePath by mutableStateOf<String>("")
//    var imageName by mutableStateOf<String>("")

    //var plantFileName by mutableStateOf<String>("")

    fun savePicture(plantFileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            takePictureUseCase.takePicture(plantFileName)
        }
    }

    fun identify(imagePath: String){
        viewModelScope.launch(Dispatchers.IO) {
           ( plantNetRepository.identifyPlant(imagePath))?.let { plant ->
               achievementsRepositoryImpl.checkChallenges(plant)
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
                challengeCardRepositoryImpl?.createNewChallengeCard(this, imagePath)
            }
        }
    }
}