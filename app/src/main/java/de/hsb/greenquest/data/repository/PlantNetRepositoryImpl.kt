package de.hsb.greenquest.data.repository

import de.hsb.greenquest.data.network.PlantNetDataSource
import de.hsb.greenquest.domain.repository.PlantNetRepository
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class PlantNetRepositoryImpl@Inject constructor(
    private val plantNetDataSource: PlantNetDataSource,
): PlantNetRepository {

    val pool: ExecutorService = Executors.newFixedThreadPool(4);
    override suspend fun identifyPlant(filepath: String): String{
        val f = pool.submit { plantNetDataSource.getPlantInfo(filepath) }
        return ""
    }

}