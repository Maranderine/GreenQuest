package de.hsb.greenquest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity

@Database(entities = [PlantPictureEntity::class], version = 1, exportSchema = false)
abstract class GreenQuestDB: RoomDatabase() {
    abstract fun plantPictureDao(): PlantPictureDao
}