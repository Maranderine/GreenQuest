package de.hsb.greenquest.data.repository

import android.net.Uri
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.File

/**
 * intermediary class to use the plantNet Datasource
 * handles buisness logic of sending http request to the API to identify a plant
 */
class PlantNetRepositoryImpl@Inject constructor(
    private val plantNetDataSource: PlantNetDataSource,
    private val parser: JsonParser = JsonParser()
): PlantNetRepository {

    /**
     * identifies plant
     * @param String filepath of plant picture
     */
    override suspend fun identifyPlant(filepath: String): Plant?{
        val gson: Gson = Gson()
        val listType = object : TypeToken<List<String>>() {}.type

        val file = File(filepath)
        val uri = Uri.fromFile(file)

        // call API
        val results = plantNetDataSource.getPlantInfo(filepath)

        // map JSON result to plant object
        val resultPlant = results?.let{ it ->
            (JsonParser.parseString(it) as JsonObject)
                .getAsJsonArray("results")[0].asJsonObject
                .let {

                    val commonNames = it.getAsJsonObject("species").getAsJsonArray("commonNames")
                    val listOfCommonNames = mutableListOf<String>()
                    for(commonName in commonNames){
                        listOfCommonNames.add(commonName.asString)
                    }

                    val species = it.getAsJsonObject("species").getAsJsonPrimitive("scientificNameWithoutAuthor").asString

                    Plant(
                        name = it.getAsJsonObject("species").getAsJsonArray("commonNames")[0].asString,
                        commonNames = listOfCommonNames,
                        species = species,
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
}