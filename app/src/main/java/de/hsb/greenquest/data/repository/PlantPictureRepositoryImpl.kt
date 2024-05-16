package de.hsb.greenquest.data.repository

import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantPictureRepositoryImpl @Inject constructor(
    private val plantPictureDao: PlantPictureDao
): PlantPictureRepository {
    override suspend fun savePlantPicture(plant: Plant) {
        plantPictureDao.savePlantPicture(plant.toPlantPictureEntity())
    }

    override fun getAllPlantPictures(): Flow<MutableList<Plant>> {
        return plantPictureDao.getAllPlantPictures().map { plantPictureEntities ->
            plantPictureEntities.map { it.toPlant() }.toMutableList()
        }
    }

    private fun PlantPictureEntity.toPlant(): Plant = Plant(
        name = name,
        description = description,
        imagePath = filePath,
        favorite = favorite
    )

    private fun Plant.toPlantPictureEntity(): PlantPictureEntity {
        return PlantPictureEntity(
            name = name,
            description = description,
            filePath = imagePath,
            favorite = favorite
        )
    }
}