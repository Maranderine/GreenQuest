package de.hsb.greenquest.data.network

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * saves and loads data for challenge cards from a cloud firestore database
 */
class ChallengeCardDataSource @Inject constructor(){

    // instance of the database
    private val db = Firebase.firestore

    fun saveChallengeCardData(imagePath: String, name: String, hint: String, location: String){

        // create firestore document
        val card = hashMapOf(
            "imagePath" to imagePath,
            "name" to name,
            "hint" to hint,
            "location" to location
        )

        // save to database
        db.collection("challengeCards")
            .add(card)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    /**
     * get all challenge cards documents that are stored in the cloud database
     *
     * @return List<DocumentSnapshot> List that contains the data of challenge card documents
     */
    suspend fun getChallengeCards(): List<DocumentSnapshot>{

        val x = db.collection("challengeCards").get().await().documents
        return x
    }
}