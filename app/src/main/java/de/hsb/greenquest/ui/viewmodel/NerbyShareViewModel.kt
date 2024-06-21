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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import de.hsb.greenquest.domain.model.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets


@HiltViewModel
class NearbyViewModel @Inject constructor(
    application: Application,
    private val connectionsClient: ConnectionsClient // Injecting ConnectionsClient
) : AndroidViewModel(application) {

    private val TEXT_PAYLOAD_TYPE: Byte = 1
    private val IMAGE_PAYLOAD_TYPE: Byte = 2
    private val strategy = Strategy.P2P_STAR

    private val _status = mutableStateOf("Idle")
    val status: State<String> = _status

    private val _endpoints = mutableStateOf(listOf<String>())
    val endpoints: State<List<String>> = _endpoints

    private val _receivedDebugMessage = MutableLiveData<String>()
    val receivedDebugMessage: LiveData<String>
        get() = _receivedDebugMessage

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
            Log.d("NearbyViewModel", "Connection initiated with endpoint: $endpointId, Info: $connectionInfo")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            messageToSend?.let {
                Log.d("NearbyViewModel", "Sending message: $it")
                sendDebugMessage(getApplication<Application>(), endpointId, it)
            }
            currentEndpointId = endpointId
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                _status.value = "Connected to $endpointId"
                Log.d("NearbyViewModel", "Connection successful with endpoint: $endpointId")
            } else {
                _status.value = "Connection failed"
                Log.d("NearbyViewModel", "Connection failed with endpoint: $endpointId, Status: ${result.status}")
            }
        }

        override fun onDisconnected(endpointId: String) {
            _status.value = "Disconnected from $endpointId"
            currentEndpointId = null
            Log.d("NearbyViewModel", "Disconnected from endpoint: $endpointId")
            stopAdvertising()
            stopDiscovery()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d("NearbyViewModel", "Payload received from endpoint: $endpointId, Payload: $payload")

            when (payload.type) {
                Payload.Type.BYTES -> {
                    val data = payload.asBytes() ?: return
                    val firstByte = data.firstOrNull() ?: return
                    when (firstByte) {
                        TEXT_PAYLOAD_TYPE -> {
                            val debugMessage = String(data.copyOfRange(1, data.size), Charsets.UTF_8)
                            _receivedDebugMessage.postValue(debugMessage)
                            Log.d("NearbyViewModel", "Received text payload: $debugMessage")
                        }
                        IMAGE_PAYLOAD_TYPE -> {
                            val imageData = data.copyOfRange(1, data.size)
                            processImagePayload(endpointId, imageData)
                        }
                        else -> {
                            Log.d("NearbyViewModel", "Received unknown payload type")
                            // Handle unknown payload type here
                            handleUnknownPayload(endpointId, data)
                        }
                    }
                }
                Payload.Type.FILE -> {
                    Log.d("NearbyViewModel", "Received file payload. This scenario is not handled in the current implementation.")
                    // Handle file payload if needed
                    handleFilePayload(endpointId, payload)
                }
                else -> {
                    Log.d("NearbyViewModel", "Received unknown payload type: ${payload.type}")
                    // Handle unknown payload type here
                    handleUnknownPayload(endpointId, null)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    val progress = update.bytesTransferred * 100 / update.totalBytes
                    Log.d("NearbyViewModel", "Transfer in progress: $progress%")
                }
                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d("NearbyViewModel", "Transfer completed successfully.")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.d("NearbyViewModel", "Transfer failed.")
                }
                PayloadTransferUpdate.Status.CANCELED -> {
                    Log.d("NearbyViewModel", "Transfer canceled.")
                }
            }
        }

        private fun handleUnknownPayload(endpointId: String, data: ByteArray?) {
            // Implement your logic to handle unknown payload types
            // For example:
            // Log the data if available
            if (data != null) {
                Log.d("NearbyViewModel", "Unknown payload data: ${String(data, Charsets.UTF_8)}")
            } else {
                Log.d("NearbyViewModel", "Unknown payload received with no data.")
            }
        }

        private fun handleFilePayload(endpointId: String, payload: Payload) {
            // Implement your logic to handle file payloads
            // For example:
            // Log the file payload details
            Log.d("NearbyViewModel", "File payload received: ${payload.id}")
        }
    }

    private fun processImagePayload(endpointId: String, imageData: ByteArray) {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver

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
        var imageUri: Uri? = null
        try {
            imageUri = contentResolver.insert(contentUri, contentValues)
        } catch (e: IOException) {
            Log.e("NearbyViewModel", "Error inserting image into MediaStore", e)
        }

        // If insertion was successful, open OutputStream and write image data
        imageUri?.let { uri ->
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    // Resize image to optimize payload size
                    val resizedImage = resizeImageForPayload(imageData)
                    outputStream.write(resizedImage)
                    Log.d("NearbyViewModel", "Image saved to MediaStore: $uri, Size: ${resizedImage.size} bytes")
                }
            } catch (e: IOException) {
                Log.e("NearbyViewModel", "Error writing image data to MediaStore", e)
            }
        } ?: run {
            Log.e("NearbyViewModel", "Failed to insert image into MediaStore")
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
            Log.d("NearbyViewModel", "Advertising started successfully.")
        }.addOnFailureListener { e ->
            _status.value = "Advertising failed: ${e.message}"
            advertisingStarted = false
            Log.d("NearbyViewModel", "Advertising failed: ${e.message}")
        }
    }

    fun stopAdvertising() {
        if (advertisingStarted) {
            connectionsClient.stopAdvertising()
            _status.value = "Stopped advertising"
            advertisingStarted = false
            Log.d("NearbyViewModel", "Advertising stopped.")
        }
    }

    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startDiscovery(
            getApplication<Application>().packageName, object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    Log.d("NearbyViewModel", "Endpoint found: $endpointId, Info: $info")
                    connectionsClient.requestConnection("DeviceName", endpointId, connectionLifecycleCallback)
                    _endpoints.value = _endpoints.value + endpointId
                }

                override fun onEndpointLost(endpointId: String) {
                    Log.d("NearbyViewModel", "Endpoint lost: $endpointId")
                    _endpoints.value = _endpoints.value - endpointId
                }
            }, discoveryOptions
        ).addOnSuccessListener {
            _status.value = "Discovering..."
            discoveringStarted = true
            Log.d("NearbyViewModel", "Discovery started successfully.")
        }.addOnFailureListener { e ->
            _status.value = "Discovery failed: ${e.message}"
            discoveringStarted = false
            Log.d("NearbyViewModel", "Discovery failed: ${e.message}")
        }
    }

    fun stopDiscovery() {
        if (discoveringStarted) {
            connectionsClient.stopDiscovery()
            _status.value = "Stopped discovering"
            discoveringStarted = false
            Log.d("NearbyViewModel", "Discovery stopped.")
        }
    }

    fun disconnect() {
        currentEndpointId?.let {
            connectionsClient.disconnectFromEndpoint(it)
            _status.value = "Disconnecting from $it..."
            Log.d("NearbyViewModel", "Disconnecting from endpoint: $it")
            stopAdvertising()
            stopDiscovery()
        }
    }

    private fun sendDebugMessage(context: Context, endpointId: String, plant: Plant) {
        // Prepare textual data payload
        val plantData = plant.toString() // Get the textual representation of the Plant object
        val dataPayload = Payload.fromBytes(plantData.toByteArray(StandardCharsets.UTF_8))
        Log.d("NearbyViewModel", "Sending text payload: $plantData")

        // Log the type of payload being sent
        Log.d("NearbyViewModel", "Sending payload of type: Text")

        // Prepare image payload
        val imageUri = plant.imagePath // Assuming imagePath is of type Uri
        Log.d("NearbyViewModel", "Preparing to send image payload, URI: $imageUri")
        val imagePayload = imageUri?.let { uri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val imageBytes = inputStream.readBytes()
                    Log.d("NearbyViewModel", "Read image bytes successfully, size: ${imageBytes.size}")
                    val resizedImage = resizeImageForPayload(imageBytes)
                    Payload.fromBytes(resizedImage)
                } else {
                    Log.d("NearbyViewModel", "Input stream is null")
                    null
                }
            } catch (e: Exception) {
                Log.e("NearbyViewModel", "Exception occurred while reading image bytes: ${e.message}", e)
                null
            }
        }

        // Send both payloads if imagePayload is not null
        if (imagePayload != null) {
            connectionsClient.sendPayload(endpointId, dataPayload)
            connectionsClient.sendPayload(endpointId, imagePayload)
            Log.d("NearbyViewModel", "Sent both text and image payloads to endpoint: $endpointId")
            // Log the type of payload being sent
            Log.d("NearbyViewModel", "Sending payload of type: Image")
        } else {
            Log.d("NearbyViewModel", "Failed to prepare image payload for sending")
        }
    }

    private fun resizeImageForPayload(imageData: ByteArray): ByteArray {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

        // Calculate the inSampleSize to reduce image dimensions
        options.inSampleSize = calculateInSampleSize(options, 1024, 1024)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val resizedBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size, options)

        // Convert resized bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        resizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val resizedImage = outputStream.toByteArray()

        // Recycle the bitmap to free up memory
        resizedBitmap?.recycle()

        return resizedImage
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}