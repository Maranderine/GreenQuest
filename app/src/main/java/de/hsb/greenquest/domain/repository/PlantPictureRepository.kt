package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import kotlinx.coroutines.flow.Flow

interface PlantPictureRepository {
    suspend fun savePlantPicture(entity: PlantPictureEntity)
    fun getAllPlantPictures(): Flow<MutableList<PlantPictureEntity>>
}