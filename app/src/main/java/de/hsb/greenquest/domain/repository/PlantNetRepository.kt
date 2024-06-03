package de.hsb.greenquest.domain.repository

interface PlantNetRepository {
    suspend fun identifyPlant(filepath: String): String
}