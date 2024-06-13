package de.hsb.greenquest.data.repository

import android.util.Log
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.CommonNameEntity
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.entity.PlantPictureWithCommonNames
import de.hsb.greenquest.data.local.mediastore.PlantPictureMediaStoreLoader
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlantPictureRepositoryImpl @Inject constructor(
    private val plantPictureDao: PlantPictureDao,
    private val plantPictureMediaStoreLoader: PlantPictureMediaStoreLoader
): PlantPictureRepository {
    override suspend fun savePlantPicture(plant: Plant) {
        val plantPictureId = plantPictureDao.insertPlantPicture(plant.toPlantPictureEntity())
        for (name in plant.commonNames) {
            val commonNameEntity = CommonNameEntity(plantPictureId = plantPictureId.toInt(), commonName = name)
            plantPictureDao.insertCommonName(commonNameEntity)
        }
    }

    override suspend fun getAllPlantPictures(): Flow<List<Plant>> {
        return plantPictureDao.getAllPlantPicturesWithCommonNames().map {
            plantWithCommonName -> plantWithCommonName.map { it.toPlant() }
        }
    }

    override suspend fun updatePlantPicture(plant: Plant) {
        plantPictureDao.updatePlantPicture(
            name = plant.name,
            favorite = plant.favorite,
            description = plant.description,
            species = plant.species
        )
    }

    override suspend fun deletePlantPicture(plant: Plant) {

        val plantEntity = plantPictureDao.getPlantPictureWithCommonNameByName(name = plant.name).firstOrNull()

        plantEntity?.let {
            plantPictureMediaStoreLoader.deletePlantPicture(plant = plant)
            plantPictureDao.deletePlantPicture(plantPicture = plantEntity.plantPicture)

            for (commonNameEntity in plantEntity.commonNames) {
                plantPictureDao.deleteCommonName(commonName = commonNameEntity)
            }
        }
    }

    private suspend fun PlantPictureWithCommonNames.toPlant(): Plant {
        val mediaStorePlantPictures = plantPictureMediaStoreLoader.getAllPlantPictures()

        val mediaStorePlantPicture = mediaStorePlantPictures.map { plant ->
            plant.find { it.name == plantPicture.name }
        }.firstOrNull() // Collect the first matching plant

        val imagePath = mediaStorePlantPicture?.imagePath

        return Plant(
            name = plantPicture.name,
            commonNames = commonNames.map { it.commonName },
            species = plantPicture.species,
            description = plantPicture.description,
            imagePath = imagePath,
            favorite = plantPicture.favorite
        )
    }

    private fun Plant.toPlantPictureEntity(): PlantPictureEntity {
        return PlantPictureEntity(
            name = name,
            species = species,
            description = description,
            favorite = favorite,
        )
    }
}