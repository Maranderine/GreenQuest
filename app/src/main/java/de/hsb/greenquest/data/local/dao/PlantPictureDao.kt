package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.hsb.greenquest.data.local.entity.CommonNameEntity
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.entity.PlantPictureWithCommonNames
import de.hsb.greenquest.data.local.utils.DataBaseConstants.PLANT_PICTURE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantPictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantPicture(plantPicture: PlantPictureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommonName(commonName: CommonNameEntity)

    @Transaction
    @Query("SELECT * FROM $PLANT_PICTURE_TABLE WHERE id = :id")
    fun getPlantPictureWithCommonNames(id: Int): Flow<PlantPictureWithCommonNames>

    @Transaction
    @Query("SELECT * FROM $PLANT_PICTURE_TABLE WHERE name = :name")
    fun getPlantPictureWithCommonNameByName(name: String): Flow<PlantPictureWithCommonNames>

    @Transaction
    @Query("SELECT * FROM $PLANT_PICTURE_TABLE")
    fun getAllPlantPicturesWithCommonNames(): Flow<List<PlantPictureWithCommonNames>>

    @Query("UPDATE $PLANT_PICTURE_TABLE SET description = :description, favorite = :favorite WHERE name = :name")
    suspend fun updatePlantPicture(name: String, description: String, favorite: Boolean)

    @Update
    suspend fun updateCommonName(commonName: CommonNameEntity)

    @Delete
    suspend fun deletePlantPicture(plantPicture: PlantPictureEntity)

    @Delete
    suspend fun deleteCommonName(commonName: CommonNameEntity)

}