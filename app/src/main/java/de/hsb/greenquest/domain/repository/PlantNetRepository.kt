package de.hsb.greenquest.domain.repository

import de.hsb.greenquest.domain.model.Plant

interface PlantNetRepository {
    suspend fun identifyPlant(filepath: String): Plant?
}