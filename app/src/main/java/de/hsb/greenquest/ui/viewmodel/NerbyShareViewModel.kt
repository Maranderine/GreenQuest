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
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.nio.charset.StandardCharsets


@HiltViewModel
class NearbyViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val TEXT_PAYLOAD_TYPE: Byte = 1
    private val IMAGE_PAYLOAD_TYPE: Byte = 2
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(application.applicationContext)
    private val strategy = Strategy.P2P_STAR

    private val _status = mutableStateOf("Idle")
    val status: State<String> = _status

    private val _endpoints = mutableStateOf(listOf<String>())
    val endpoints: State<List<String>> = _endpoints

    private val _receivedDebugMessage = mutableStateOf<String>("") // State to hold received debug message
    val receivedDebugMessage: State<String> = _receivedDebugMessage

    private var messageToSend: Plant? = null // Message to send

    private var currentEndpointId: String? = null // Store the currently connected endpoint ID

    private var advertisingStarted = false // Track if advertising is currently active
    private var discoveringStarted = false // Track if discovering is currently active
    private val _receivedImageData = MutableStateFlow<ByteArray?>(null)
    val receivedImageData: StateFlow<ByteArray?> = _receivedImageData

    // Function to receive image data from payload
    fun receiveImageData(imageData: ByteArray) {
        viewModelScope.launch {
            _receivedImageData.emit(imageData)
        }
    }
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            messageToSend?.let {
                println(it.toString())
                sendDebugMessage(getApplication<Application>(), endpointId, it)
            }
            currentEndpointId = endpointId
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                _status.value = "Connected to $endpointId"
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
            when (payload.type) {
                Payload.Type.BYTES -> {
                    println(payload)
                    val data = payload.asBytes() ?: return
                    val firstByte = data.firstOrNull() ?: return
                    when (firstByte) {
                        TEXT_PAYLOAD_TYPE -> {
                            val debugMessage = String(data.copyOfRange(1, data.size), Charsets.UTF_8)
                            _receivedDebugMessage.value = debugMessage
                            println("Received text payload: $debugMessage")
                        }
                        IMAGE_PAYLOAD_TYPE -> {
                            val imageData = data.copyOfRange(1, data.size)
                            processImagePayload(endpointId, imageData)
                        }
                        else -> {
                            // Handle unknown payload type
                        }
                    }
                }
                Payload.Type.FILE -> {
                    // Handle file payload if needed
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val progress = update.bytesTransferred * 100 / update.totalBytes
                    println("Transfer in progress: $progress%")
                }
                PayloadTransferUpdate.Status.SUCCESS -> {
                    println("Transfer completed successfully.")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    println("Transfer failed.")
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    println("Transfer canceled.")
                }
            }
        }
    }

    // Function to process image payload and save to MediaStore
    private fun processImagePayload(endpointId: String, imageData: ByteArray) {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver
        println("Image DATA "+imageData)
        // Prepare image file metadata
        val name = "GreenQuest.jpeg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GreenQuest")
            }
        }

        // Define the content URI for inserting an image
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // Insert image into MediaStore
        contentResolver.insert(contentUri, contentValues)?.also { uri ->
            // Open an OutputStream to write the image data to the specified content URI
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(imageData)
                println("Image saved to MediaStore: $uri")
            }
        } ?: run {
            println("Failed to insert image into MediaStore")
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

    private fun sendDebugMessage(context: Context, endpointId: String, plant: Plant) {
        // Prepare textual data payload
        val plantData = plant.toString() // Get the textual representation of the Plant object
        val dataPayload = Payload.fromBytes(plantData.toByteArray(StandardCharsets.UTF_8))
        println(plantData+ " PLANT AND BYTE" + dataPayload)

        // Prepare image payload
        val imageUri = plant.imagePath // Assuming imagePath is of type Uri
        println("SEND URI "+imageUri)
        val imagePayload = imageUri?.let { uri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val imageBytes = inputStream.readBytes()
                    Payload.fromBytes(imageBytes)
                } else {
                    println("Input stream is Null")
                    null
                }
            } catch (e: Exception) {
                println("Handle exceptions, such as file not found or permission denied")
                e.printStackTrace()
                null
            }
        }

        // Send both payloads if imagePayload is not null
        imagePayload?.let {
            connectionsClient.sendPayload(endpointId, dataPayload)
            connectionsClient.sendPayload(endpointId, imagePayload)
        }
    }
}