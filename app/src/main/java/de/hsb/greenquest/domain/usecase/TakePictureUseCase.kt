package de.hsb.greenquest.domain.usecase

import android.util.Log
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import javax.inject.Inject

class TakePictureUseCase @Inject constructor(
    private val repository: PlantPictureRepository
) {
    suspend fun takePicture(plantFileName: String, species: String = "apiSpecies", commonNames: List<String> = listOf("apiName1", "apiName2")) {
        Log.d("plantFileName", plantFileName)
        repository.savePlantPicture(Plant(name = plantFileName, imagePath = null, description = "", favorite = false, species = species, commonNames = commonNames))
    }
}