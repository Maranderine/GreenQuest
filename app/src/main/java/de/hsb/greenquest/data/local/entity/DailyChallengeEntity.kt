package de.hsb.greenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dailyChallenges")
data class DailyChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val type: String,
    val requiredCount: Int,
)