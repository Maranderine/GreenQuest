package de.hsb.greenquest.domain.usecase

import android.util.Log
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantIdentificationException(message: String) : Exception(message)

/**
 * buisness logic behind taking a picture in the camera screen
 */
class TakePictureUseCase @Inject constructor(
    private val repository: PlantPictureRepository,
    private val plantNetRepository: PlantNetRepository
) {
    suspend fun takePicture(plantFileName: String, imagePath: String) {

        try {
            plantNetRepository.identifyPlant(imagePath)?.let { remotePlant ->
                repository.savePlantPicture(
                    Plant(
                        name = plantFileName,
                        imagePath = null,
                        description = "",
                        favorite = false,
                        species = remotePlant.species,
                        commonNames = remotePlant.commonNames
                    )
                )
            } ?: throw PlantIdentificationException("No Plant Recognized. Pleas Try Again")
        } catch (e: Exception) {
            // eg. if response from PlantNet API was 404 not found --> could not be identified

            // delete picture from gallery:
            // create dummy plant
            val plant = Plant(
                name = plantFileName,
                imagePath = null,
                description = "",
                favorite = false,
                species = "",
                commonNames = listOf("")
            )
            // save as dummy portfolio entry
            repository.savePlantPicture(plant)

            // delete portfolio entry together with picture from gallery.
            val plant1 = repository.getAllPlantPictures().map {
                it.find { p -> p.name == plant.name }
            }
            plant1.firstOrNull()?.let { repository.deletePlantPicture(it) }

            Log.d("DELTETE", "")
            throw PlantIdentificationException("No Plant Recognized. Pleas Try Again")
        }

    }
}