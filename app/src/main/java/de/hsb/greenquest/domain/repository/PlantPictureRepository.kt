package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantPictureRepository {
    suspend fun savePlantPicture(plant: Plant)
    suspend fun getAllPlantPictures(): Flow<List<Plant>>
    suspend fun updatePlantPicture(plant: Plant)

}