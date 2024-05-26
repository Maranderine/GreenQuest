package de.hsb.greenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class LocalChallengeEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val Plant: String,
    val requiredCount: Int,
    val progress: Int,
    val date: String?
)