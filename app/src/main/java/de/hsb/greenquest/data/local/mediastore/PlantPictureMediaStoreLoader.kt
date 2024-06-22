package de.hsb.greenquest.data.local.mediastore

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PlantPictureMediaStoreLoader @Inject constructor(
    private val applicationContext: Context
)/*: PlantPictureRepository*/ {

    private val _plantPicturesFlow = MutableStateFlow<MutableList<Plant>>(mutableListOf())
    val plantPicturesFlow: StateFlow<MutableList<Plant>> = _plantPicturesFlow

    init {
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                loadPicturesFromMediaStore()
            }
        }

        applicationContext.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )

        // Initial load
        loadPicturesFromMediaStore()
    }

    /*override*/ suspend fun savePlantPicture(plant: Plant) {
        // TODO: Implement save functionality
    }

    /*override*/ fun getAllPlantPictures(): Flow<MutableList<Plant>> {
        return plantPicturesFlow
    }

    fun deletePlantPicture(plant: Plant) {
        val contentResolver = applicationContext.contentResolver

        plant.imagePath?.let { uri ->
            contentResolver.delete(uri, null, null) // Delete the picture from the MediaStore
        }
    }

    fun deletePicture(uri: Uri) {
        val contentResolver = applicationContext.contentResolver

        contentResolver.delete(uri, null, null) // Delete the picture from the MediaStore

    }

    private fun loadPicturesFromMediaStore() {
        val context = applicationContext
        val contentResolver = context.contentResolver

        // Define the projection for querying the MediaStore
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.IS_FAVORITE
        )

        val selection = MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?"
        val selectionArgs = arrayOf("Pictures/GreenQuest/")

        // Query the MediaStore
        val plantImageList: MutableList<Plant> = mutableListOf()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.apply {
            val idColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val isFavoriteColumnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.IS_FAVORITE)

            // Process each row in the cursor
            while (moveToNext()) {
                val id = getLong(idColumnIndex)
                val name = getString(nameColumnIndex)
                val favorite: Boolean = getString(isFavoriteColumnIndex).toInt() != 0
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                // Add the Plant object to the list
                plantImageList.add(Plant(name = name, imagePath = contentUri, favorite = favorite, description = "", commonNames = listOf(), species = ""))
            }

            // Close the cursor
            close()
        }

        // Emit the list of Plant objects
        _plantPicturesFlow.update { plantImageList }
    }
}

