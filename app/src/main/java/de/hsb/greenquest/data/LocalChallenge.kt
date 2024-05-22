package de.hsb.greenquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "challenges")
data class LocalChallenge (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val Plant: String,
    val requiredCount: Int,
    val progress: Int,
    val date: String?
)