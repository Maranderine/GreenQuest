package de.hsb.greenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activeDailyChallenges")
data class ActiveDailyChallengeEntity(
    @PrimaryKey(autoGenerate = false)
    val challengeId: Int,
    val progress: Int,
    val date: String?
)