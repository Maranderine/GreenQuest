package de.hsb.greenquest.data.network

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ChallengeCardPicturesDataSource @Inject constructor() {

    suspend fun uploadImageToStorage(imagePath: String): String{
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()
        var uniqueID = UUID.randomUUID().toString()


        val imageName = "$uniqueID.jpeg"
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