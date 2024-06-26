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

/**
 * Responsible for downloading or uploading images associated to a challenge card.
 * from/ to Firebase cloud storage
 */
class ChallengeCardPicturesDataSource @Inject constructor() {

    /**
     * updloads
     *
     * @param String path of the image to upload
     * @return String path in storage
     */
    suspend fun uploadImageToStorage(imagePath: String): String{
        // instance of storage
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference()

        // unique id for image in storage
        var uniqueID = UUID.randomUUID().toString()

        // defining the path in the storage where to save
        val imageName = "$uniqueID.jpeg"
        val storagePath = "images/challengeCards/$imageName"

        // get reference to location
        val imageRef: StorageReference = storageRef.child(storagePath)

        val imageUri = Uri.fromFile(File(imagePath))

        // start updloading
        val uploadTask: UploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress: Double =
                100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()
        }.addOnSuccessListener { taskSnapshot ->
            // listener is triggered when the file is uploaded successfully.
            imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                val imageUrl: String = uri.toString()
            }
        }.addOnFailureListener { exception -> print("UPLOAD FAILED")}
        return storagePath
    }

    /**
     * download
     *
     * @param String path to image in cloud storage
     * @return File of Image if successfull, else null
     */
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