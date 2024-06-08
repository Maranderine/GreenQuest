package de.hsb.greenquest.data.repository

import android.net.Uri
import de.hsb.greenquest.data.network.PlantNetDataSource
import de.hsb.greenquest.domain.repository.PlantNetRepository
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.File

class PlantNetRepositoryImpl@Inject constructor(
    private val plantNetDataSource: PlantNetDataSource,
    private val parser: JsonParser = JsonParser()
): PlantNetRepository {

    val pool: ExecutorService = Executors.newFixedThreadPool(4);
    override suspend fun identifyPlant(filepath: String): Plant?{
        //val gson: Gson = Gson()
        val file = File(filepath)
        val uri = Uri.fromFile(file)
        
        val results = plantNetDataSource.getPlantInfo(filepath)
        val resultPlant = results?.let{ it ->
            (JsonParser.parseString(it) as JsonObject)
                .getAsJsonArray("results")[0].asJsonObject
                .let {
                    Plant(
                        name = it.getAsJsonObject("species").getAsJsonArray("commonNames")[0].asString,
                        description = "",
                        imagePath = uri,
                        favorite = false
                    )
                }.also{
                    println("Plant: $it")
                }
        }
        return resultPlant
    }

    fun mapJsonToPlant(){
        //TODO mapping function
    }

}

// TODO map json result to plant object
// TODO create event data class
// TODO create event dispatcher with mutablestateflow