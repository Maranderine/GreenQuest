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

/**
 * class responsible for communication with the PlantNet API
 * uses OKHttp library
 *
 * https://square.github.io/okhttp/recipes/
 */
class PlantNetDataSource @Inject constructor() {

    // isntance of OkHttpClient
    private val client = OkHttpClient()

    // constants used in the Post request
    private val PROJECT = "all"
    private val APIKEY = "2b10RYl9lUThHrzmylGhx2juTO"
    private val POST_URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?include-related-images=false&no-reject=false&lang=en&api-key=" + APIKEY

    fun getPlantInfo(filename: String): String? {

        // get picture
        val file = File(filename)

        // build Mulitpart Http post Request body
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("images", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .build()

        // build post request itslef
        val request: Request = Request.Builder()
            .url(POST_URL)
            .post(req)
            .build()

        val requestString = request.body.toString()
        println("CALL API WITH BODY: " + request.url + " "+ requestString)

        // call API
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val data = response.body!!.string()
            println("RESPONSE: $data")
            return data
        }
    }
}
