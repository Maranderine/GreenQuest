package de.hsb.greenquest.data.network

import android.util.Log
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import javax.inject.Inject

class PlantNetDataSource @Inject constructor() {

    private val client = OkHttpClient()
    private val PROJECT = "all"
    private val APIKEY = "2b10RYl9lUThHrzmylGhx2juTO"
    private val GET_URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT +  "?images=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fb%2Fbf%2FSucculent_plant.JPG&include-related-images=false&no-reject=false&lang=en&api-key=" + APIKEY
    private val POST_URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?include-related-images=false&no-reject=false&lang=en&api-key=" + APIKEY

    //suspend fun loadTasks(): List<NetworkTask> = accessMutex.withLock
    fun getPlantInfo2(filepath: String): String {
        val file = File(filepath)
        val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull();

        val request = Request.Builder()
            .url(POST_URL)
            .post(file.asRequestBody(MEDIA_TYPE_PNG))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println(response.body!!.string())
        }
        return "test"
    }

    fun getPlantInfo(filename: String): String? {

        val file = File(filename)

        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("images", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .build()

        val request: Request = Request.Builder()
            .url(POST_URL)
            .post(req)
            .build()

        val requestString = request.body.toString()
        println("CALL API WITH BODY: " + request.url + " "+ requestString)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val data = response.body!!.string()
            println("RESPONSE: $data")
            return data
        }
    }

    //suspend fun saveTasks(newTasks: List<NetworkTask>) = accessMutex.withLock
    suspend fun save(){
    }
}
