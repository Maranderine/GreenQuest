package de.hsb.greenquest.data.repository

import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlantPictureRepositoryImpl @Inject constructor(
    private val plantPictureDao: PlantPictureDao
): PlantPictureRepository {
    override suspend fun savePlantPicture(entity: PlantPictureEntity) {
        plantPictureDao.savePlantPicture(entity)
    }

    override fun getAllPlantPictures(): Flow<MutableList<PlantPictureEntity>> {
      return plantPictureDao.getAllPlantPictures()
    }
}