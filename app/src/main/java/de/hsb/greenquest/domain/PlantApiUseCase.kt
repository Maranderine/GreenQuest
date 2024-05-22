package de.hsb.greenquest.domain

import android.util.Log
import okhttp3.CacheControl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException


class PlantApiUseCase {
    private val client = OkHttpClient()
    private val PROJECT = "all"
    private val APIKEY = "2b10RYl9lUThHrzmylGhx2juTO"
    private val GET_URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT +  "?images=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fb%2Fbf%2FSucculent_plant.JPG&include-related-images=false&no-reject=false&lang=en&api-key=" + APIKEY
    private val POST_URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?include-related-images=false&no-reject=false&lang=en&api-key=" + APIKEY

    fun getPlantInfo(filename: String) {
        val file = File(filename)
        val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull();

        val request = Request.Builder()
            .url(POST_URL)
            .post(file.asRequestBody(MEDIA_TYPE_PNG))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            println(response.body!!.string())
        }
    }

    fun getPlantInfo2(filename: String): JSONObject {
        val file = File(filename)
        val MEDIA_TYPE_PNG: MediaType? = "image/png".toMediaTypeOrNull()

        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .build()

        val request: Request = Request.Builder()
            .url("url")
            .post(req)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        Log.d("response", "uploadImage:" + response.body!!.string())

        return JSONObject(response.body!!.string())

    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
    }
}