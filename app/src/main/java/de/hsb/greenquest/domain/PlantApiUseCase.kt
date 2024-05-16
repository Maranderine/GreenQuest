package de.hsb.greenquest.domain

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class PlantApiUseCase {
    private val client = OkHttpClient()
    private val PROJECT = ""
    private val APIKEY = ""
    private val url = ""


    fun getPlantInfo(filename: String) {
        val file = File(filename)

        val request = Request.Builder()
            .url("https://api.github.com/markdown/raw")
            .post(file.asRequestBody(MEDIA_TYPE_MARKDOWN))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
        }
    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
    }
}