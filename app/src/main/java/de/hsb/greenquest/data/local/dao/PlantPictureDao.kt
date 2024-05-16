package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.utils.DataBaseConstants.PLANT_PICTURE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantPictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlantPicture(plantPictureEntity: PlantPictureEntity)

    @Query("SELECT * FROM $PLANT_PICTURE_TABLE")
    fun getAllPlantPictures() : Flow<MutableList<PlantPictureEntity>>
}