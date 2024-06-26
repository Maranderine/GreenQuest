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

/**
 * class to handle operations on portfolio related plant images.
 * reads and saves text data from/ to rooms database
 * reads/ writes  images from/ to gallery using MediaStore API
 */
class PlantPictureRepositoryImpl @Inject constructor(
    private val plantPictureDao: PlantPictureDao,
    private val plantPictureMediaStoreLoader: PlantPictureMediaStoreLoader
): PlantPictureRepository {

    /**
     * saves plant portfolio entry data
     */
    override suspend fun savePlantPicture(plant: Plant) {

        val plantPictureId = plantPictureDao.insertPlantPicture(plant.toPlantPictureEntity())
        for (name in plant.commonNames) {
            // save all common names of plant in common Name table
            val commonNameEntity = CommonNameEntity(plantPictureId = plantPictureId.toInt(), commonName = name)
            plantPictureDao.insertCommonName(commonNameEntity)
        }
    }

    /**
     * get all complete plant portfolio entries
     */
    override suspend fun getAllPlantPictures(): Flow<List<Plant>> {
        return plantPictureDao.getAllPlantPicturesWithCommonNames().map {
            plantWithCommonName -> plantWithCommonName.map { it.toPlant() }
        }
    }

    /**
     * updates portfolio entry data
     *@param plant of which the data that is saved in the database should be changed
     */
    override suspend fun updatePlantPicture(plant: Plant) {
        print("INSIDE UPDATE FUNCTION")
        plantPictureDao.updatePlantPicture(
            name = plant.name,
            favorite = plant.favorite,
            description = plant.description,
            species = plant.species
        )
    }

    /**
     * deletes complete portfolio entry (data and image)
     *@param plant of which the entry should be deleted
     */
    override suspend fun deletePlantPicture(plant: Plant) {

        val plantEntity = plantPictureDao.getPlantPictureWithCommonNameByName(name = plant.name).firstOrNull()

        plantEntity?.let {
            Log.d("DELTETE1", "")
            //delete image form gallery
            plantPictureMediaStoreLoader.deletePlantPicture(plant = plant)
            //delete data from database
            plantPictureDao.deletePlantPicture(plantPicture = plantEntity.plantPicture)

            // delete respective common names
            for (commonNameEntity in plantEntity.commonNames) {
                plantPictureDao.deleteCommonName(commonName = commonNameEntity)
            }
        }
    }

    // map plant entity to plant data object.
    // gets image path of respective portfolio image by quering media store for picture with same name
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