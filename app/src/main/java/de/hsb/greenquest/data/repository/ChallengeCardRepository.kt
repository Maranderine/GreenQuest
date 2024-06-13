package de.hsb.greenquest.data.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.model.challengeCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ChallengeCardRepository @Inject constructor(
    private val firebaseApp: FirebaseApp?
) {

    private val db = Firebase.firestore

    suspend fun createChallengeCard(plant: Plant, imagePath: String, hint: String){
        val imgPathInStorage = this.uploadImageToStorage(imagePath)
        this.saveChallengeCardData(imagePath = imgPathInStorage, name = plant.name, hint = hint, location = "")
    }

    suspend fun getChallengeCardByIndex(idx: Int): challengeCard? {

        return getChallengeCardDataByIndex(idx)?.let { data ->
            downloadImageFromStorage(data.get("imagePath") as String)?.let{
                File ->
                print("GET IMAGE")
                return challengeCard(
                    img = File,
                    name = data.get("name") as String,
                    hint = data.get("hint") as String,
                    location = data.get("location") as String
                )
            }
        }
        return null
    }

    fun saveChallengeCardData(imagePath: String, name: String, hint: String, location: String){
        val card = hashMapOf(
            "imagePath" to imagePath,
            "name" to name,
            "hint" to hint,
            "location" to location
        )

        db.collection("challengeCards")
            .add(card)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    suspend fun getChallengeCardDataByIndex(idx: Int): Map<String, Any>?{
        val cards = getChallengeCards()
        if(cards.size > 0){
            return cards.get(idx).data
        }
        else{
            return null
        }
    }

    suspend fun getChallengeCards(): List<DocumentSnapshot>{

        val x = db.collection("challengeCards").get().await().documents
        return x
    }

    suspend fun countChallengeCards(): Int{
        try{
          val lastImagePath =  db.collection("challengeCards").get().await().documents.last().data?.get("imagePath") as String
            val re = Regex("(?<=images/challengeCards/image)[0-9]+(?=.jpeg)")
            var returnVal = re.find(lastImagePath)?.value
            var returnValue = returnVal?.toIntOrNull()?.plus(1)
            return returnValue?: 0
        }catch(err: Exception){
            return 0
        }
    }
    //TODO parse string and increase

    suspend fun uploadImageToStorage(imagePath: String): String{
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        val runVariable: Int = countChallengeCards()


        val imageName = "image$runVariable.jpeg"
        val storagePath = "images/challengeCards/$imageName"
        val imageRef: StorageReference = storageRef.child(storagePath)
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
        }.addOnFailureListener { exception -> print("UPLOAD FAILED")}
        return storagePath
    }

    suspend fun downloadImageFromStorage(imgPath: String): File?{
        try{
            val storage: FirebaseStorage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.getReference()

            val pathReference = storageRef.child(imgPath)

            val localFile = withContext(Dispatchers.IO) {
                File.createTempFile("images", "jpg")
            }
            pathReference.getFile(localFile).await()

            return localFile
        }catch (err: Error){
            return null
        }
    }
}