package de.hsb.greenquest.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.util.TableInfo
import de.hsb.greenquest.data.local.utils.DataBaseConstants.COMMON_NAME_TABLE
import de.hsb.greenquest.data.local.utils.DataBaseConstants.PLANT_PICTURE_TABLE

// TODO explanation
@Entity(
    tableName = PLANT_PICTURE_TABLE,
    indices = [Index(value = ["name"], unique = true)]
)
data class PlantPictureEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    val species: String = "",
    var description: String = "",
    var favorite: Boolean = false
)

@Entity(
    tableName = COMMON_NAME_TABLE,
)
data class CommonNameEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var plantPictureId: Int,
    var commonName: String
)

data class PlantPictureWithCommonNames(
    @Embedded val plantPicture: PlantPictureEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "plantPictureId"
    )
    val commonNames: MutableList<CommonNameEntity>
)