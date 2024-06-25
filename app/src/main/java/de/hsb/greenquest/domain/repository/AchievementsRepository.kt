package de.hsb.greenquest.domain.repository

import android.content.Context
import de.hsb.greenquest.data.repository.toChallengeCard
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

interface AchievementsRepository {

    val points: StateFlow<Int>

    fun writeTo(filename: String, fileContents: String)

    fun readFrom(filename: String): String

    fun getUserPoints(): Int?

    fun saveUserPoints()

    suspend fun checkChallenges(plant: Plant)
}