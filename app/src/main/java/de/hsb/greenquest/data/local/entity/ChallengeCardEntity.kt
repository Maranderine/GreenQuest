package de.hsb.greenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activeChallengeCards")
data class ChallengeCardEntity (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val imagePath: String,
    val location: String,
    val hint: String,
)