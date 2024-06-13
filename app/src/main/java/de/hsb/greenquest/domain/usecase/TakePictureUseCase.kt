package de.hsb.greenquest.domain.usecase

import android.util.Log
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import javax.inject.Inject

class TakePictureUseCase @Inject constructor(
    private val repository: PlantPictureRepository,
    private val plantNetRepository: PlantNetRepository
) {
    suspend fun takePicture(plantFileName: String, imagePath: String) {
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
        }
    }
}