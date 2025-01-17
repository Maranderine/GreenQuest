package de.hsb.greenquest.ui.viewmodel

import androidx.compose.runtime.State
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import javax.inject.Inject
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hsb.greenquest.data.local.mediastore.PlantPictureMediaStoreLoader
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.random.Random
import de.hsb.greenquest.ui.screen.rotateBitmap


@HiltViewModel
class NearbyViewModel @Inject constructor(
    application: Application,
    plantPictureMediaStoreLoader: PlantPictureMediaStoreLoader,
    plantPictureRepository: PlantPictureRepository
) : AndroidViewModel(application) {

    private val TAG = "NearbyViewModel"

    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(application.applicationContext)
    private val strategy = Strategy.P2P_STAR

    private val _status = mutableStateOf("Idle")
    val status: State<String> = _status

    private val _endpoints = mutableStateOf(listOf<String>())
    val endpoints: State<List<String>> = _endpoints

    private val _receivedDebugMessage = mutableStateOf<String>("")
    val receivedDebugMessage: State<String> = _receivedDebugMessage

    private val _receivedImageBitmap = mutableStateOf<ImageBitmap?>(null)
    val receivedImageBitmap: State<ImageBitmap?> = _receivedImageBitmap

    private var messageToSend: Plant? = null
    private var currentEndpointId: String? = null
    private var advertisingStarted = false
    private var discoveringStarted = false

    private var debugMessage by mutableStateOf("")

    fun resetState() {
        _status.value = "Idle"
        _endpoints.value = listOf()
        _receivedDebugMessage.value = ""
        _receivedImageBitmap.value = null
        messageToSend = null
        currentEndpointId = null
        advertisingStarted = false
        discoveringStarted = false
        debugMessage = ""
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            currentEndpointId = endpointId
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                _status.value = "Connected to $endpointId"
                messageToSend?.let {
                    sendDebugMessage(endpointId, it)
                }
            } else {
                _status.value = "Connection failed"
            }
        }

        override fun onDisconnected(endpointId: String) {
            _status.value = "Disconnected from $endpointId"
            currentEndpointId = null
            stopAdvertising()
            stopDiscovery()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                debugMessage = String(payload.asBytes()!!)
                _receivedDebugMessage.value = debugMessage
            }
            if (payload.type == Payload.Type.STREAM) {
                val inputStream = payload.asStream()?.asInputStream()
                inputStream?.let {
                    try {
                        val buffer = ByteArray(200000)
                        var bytesRead: Int
                           val outputStream = ByteArrayOutputStream()

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }

                        if (bytesRead == -1) {

                            val imageBytes = outputStream.toByteArray()
                            var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            if (bitmap != null) {
                                bitmap = rotateBitmap(bitmap,90f)
                                _receivedImageBitmap.value = bitmap.asImageBitmap() // Convert Bitmap to ImageBitmap
                                viewModelScope.launch(Dispatchers.IO) {

                                    val tempPlant = createPlantFromString(debugMessage)

                                    val uri = plantPictureMediaStoreLoader.savePlantPicture(tempPlant.name, bitmap)

                                    plantPictureRepository.savePlantPicture(
                                        tempPlant.copy(imagePath = uri)
                                    )
                                }
                            } else {
                                Log.e(TAG, "Failed to decode byte array into Bitmap")
                            }

                            disconnect()
                            outputStream.reset()
                        }

                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(TAG, "Error reading stream: ${e.message}", e)
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            Log.e(TAG, "Error closing inputStream: ${e.message}", e)
                        }
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        }
    }

    fun startAdvertising(plant: Plant?) {
        messageToSend = plant
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startAdvertising(
            "DeviceName", getApplication<Application>().packageName, connectionLifecycleCallback, advertisingOptions
        ).addOnSuccessListener {
            _status.value = "Advertising..."
            advertisingStarted = true
        }.addOnFailureListener { e ->
            _status.value = "Advertising failed: ${e.message}"
            advertisingStarted = false
            Log.e(TAG, "startAdvertising: Advertising failed", e)
        }
    }

    fun stopAdvertising() {
        if (advertisingStarted) {
            connectionsClient.stopAdvertising()
            _status.value = "Stopped advertising"
            advertisingStarted = false
        }
    }

    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startDiscovery(
            getApplication<Application>().packageName, object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    connectionsClient.requestConnection("DeviceName", endpointId, connectionLifecycleCallback)
                    _endpoints.value = _endpoints.value + endpointId
                }

                override fun onEndpointLost(endpointId: String) {
                    _endpoints.value = _endpoints.value - endpointId
                }
            }, discoveryOptions
        ).addOnSuccessListener {
            _status.value = "Discovering..."
            discoveringStarted = true
        }.addOnFailureListener { e ->
            _status.value = "Discovery failed: ${e.message}"
            discoveringStarted = false
            Log.e(TAG, "startDiscovery: Discovery failed", e)
        }
    }

    fun stopDiscovery() {
        if (discoveringStarted) {
            connectionsClient.stopDiscovery()
            _status.value = "Stopped discovering"
            discoveringStarted = false
        }
    }

    fun disconnect() {
        currentEndpointId?.let {
            connectionsClient.disconnectFromEndpoint(it)
            _status.value = "Disconnecting from $it..."
            stopAdvertising()
            stopDiscovery()
        }
    }

    private fun sendDebugMessage(endpointId: String, plant: Plant?) {
        val payload = Payload.fromBytes(plant.toString().toByteArray())
        connectionsClient.sendPayload(endpointId, payload)
        sendImage(endpointId, plant)
    }
    private fun sendImage(endpointId: String, plant: Plant?) {
        val imageUri = plant?.imagePath // Assuming imagePath is of type Uri

        try {
            val inputStream = imageUri?.let {
                getApplication<Application>().contentResolver.openInputStream(it)
            }

            if (inputStream != null) {
                val bufferSize = (200000)

                val buffer = ByteArray(bufferSize)
                var bytesRead: Int

                val bitmap2 = BitmapFactory.decodeStream(inputStream)
                val byteArrayoutputStream = ByteArrayOutputStream()
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayoutputStream) // Use higher quality

                val byteArrayOutputStreamByteArray = byteArrayoutputStream.toByteArray()
                val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStreamByteArray) // Single InputStream instance

                while (byteArrayInputStream.read(buffer).also { bytesRead = it } > 0) {
                    val payload = Payload.fromStream(ByteArrayInputStream(buffer, 0, bytesRead))
                    connectionsClient.sendPayload(endpointId, payload)
                }
                //inputStream.reset()
                inputStream.close() // Close the InputStream
            } else {
            }
        } catch (e: Exception) {
            Log.e("NearbyViewModel", "Exception occurred while sending image: ${e.message}", e)
        }
    }

    fun createPlantFromString(input: String): Plant {
        val nameRegex = "name=([^,]+)".toRegex()
        val commonNamesRegex = "commonNames=\\[([^\\]]+)\\]".toRegex()
        val speciesRegex = "species=([^,]+)".toRegex()
        val descriptionRegex = "description=([^,]+)".toRegex()
        val imagePathRegex = "imagePath=([^,]+)".toRegex()
        val favoriteRegex = "favorite=([^\\)]+)".toRegex()

        val name = nameRegex.find(input)?.groups?.get(1)?.value ?: ""
        val commonNames = commonNamesRegex.find(input)?.groups?.get(1)?.value?.split(", ") ?: listOf()
        val species = speciesRegex.find(input)?.groups?.get(1)?.value ?: ""
        val description = descriptionRegex.find(input)?.groups?.get(1)?.value ?: ""
        val imagePath = imagePathRegex.find(input)?.groups?.get(1)?.value?.let { Uri.parse(it) }
        val favorite = favoriteRegex.find(input)?.groups?.get(1)?.value?.toBoolean() ?: false

        return Plant(name, commonNames, species, description, imagePath, favorite)
    }
}