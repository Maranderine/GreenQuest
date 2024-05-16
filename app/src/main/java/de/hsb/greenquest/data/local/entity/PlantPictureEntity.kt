package de.hsb.greenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.hsb.greenquest.data.local.utils.DataBaseConstants.PLANT_PICTURE_TABLE

@Entity(tableName = PLANT_PICTURE_TABLE)
data class PlantPictureEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var filePath: String = "",
    var favorite: Boolean = false
)
