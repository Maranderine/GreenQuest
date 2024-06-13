package de.hsb.greenquest.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hsb.greenquest.data.repository.ChallengeCardRepository
import de.hsb.greenquest.domain.repository.ChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.annotation.Nullable
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val takePictureUseCase: TakePictureUseCase,
    private val plantNetRepository: PlantNetRepository,
    private val challengeRepository: ChallengeRepository,
    private val challengeCardRepository: ChallengeCardRepository?
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
               val activeChallenges = challengeRepository.getActiveChallenges()
               activeChallenges.forEach{
                   println("compare ${it.Plant} == ${plant.name}")
                   if(it.Plant == plant.name){challengeRepository.updateChallenge(it.copy(progress = it.progress+1))}
               }
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
                challengeCardRepository?.createChallengeCard(this, imagePath, hint = "")
            }

        }
    }
}