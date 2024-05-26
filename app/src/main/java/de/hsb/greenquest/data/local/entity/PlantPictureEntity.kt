package de.hsb.greenquest.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo
import de.hsb.greenquest.data.local.utils.DataBaseConstants.PLANT_PICTURE_TABLE

//@Entity(tableName = PLANT_PICTURE_TABLE,
//    indices = { TableInfo.Index(value = {"name"}, unique = true) })

@Entity(
    tableName = PLANT_PICTURE_TABLE,
    indices = [Index(value = ["name"], unique = true)]
)
data class PlantPictureEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var favorite: Boolean = false
)
