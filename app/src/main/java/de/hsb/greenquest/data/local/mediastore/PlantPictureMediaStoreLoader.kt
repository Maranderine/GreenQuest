package de.hsb.greenquest.data.local.mediastore

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlantPictureMediaStoreLoader @Inject constructor(
    private val applicationContext: Context
)/*: PlantPictureRepository*/ {
    /*override*/ suspend fun savePlantPicture(plant: Plant) {
        //TODO("Not yet implemented")
    }

    /*override*/ fun getAllPlantPictures(): Flow<MutableList<Plant>> {
        return loadPicturesFromMediaStore()
    }

    fun deletePlantPicture(plant: Plant) {
        val contentResolver = applicationContext.contentResolver

        plant.imagePath?.let { uri ->
            contentResolver.delete(uri, null, null) // Delete the picture from the MediaStore
        }
    }

    private fun loadPicturesFromMediaStore(): Flow<MutableList<Plant>> {
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

        // Return a flow of lists of Plant objects
        return flow {
            val plantImageList: MutableList<Plant> = mutableListOf()

            // Query the MediaStore
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

//                    Log.d("TESTTEST", "DISPLAY_NAME = $name")
//                    Log.d("TESTTEST1", "CONTACT_ID = $id")
//                    Log.d("TESTTEST2", "CONTACT_PATH = ${getString(pathColumnIndex)}")
//                    Log.d("TESTTEST3", "CONTACT_FAVORITE = ${getLong(isFavoriteColumnIndex)}")
                }

                // Close the cursor
                close()
            }

            // Emit the list of Plant objects
            emit(plantImageList)
        }
    }
}

