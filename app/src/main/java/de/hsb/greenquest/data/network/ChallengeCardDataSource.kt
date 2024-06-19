package de.hsb.greenquest.data.network

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//GETS ONLINE DATA OF CHALLENGE CARDS
class ChallengeCardDataSource @Inject constructor(){
    private val db = Firebase.firestore

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
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
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
    /*suspend fun countChallengeCards(): Int{
        try{
            val lastImagePath =  db.collection("challengeCards").get().await().documents.last().data?.get("imagePath") as String
            val re = Regex("(?<=images/challengeCards/image)[0-9]+(?=.jpeg)")
            var returnVal = re.find(lastImagePath)?.value
            var returnValue = returnVal?.toIntOrNull()?.plus(1)
            return returnValue?: 0
        }catch(err: Exception){
            return 0
        }
    }*/
}