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
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import de.hsb.greenquest.domain.model.Plant
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


@HiltViewModel
class NearbyViewModel @Inject constructor(
    application: Application
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

    private var messageToSend: Plant? = null
    private var currentEndpointId: String? = null
    private var advertisingStarted = false
    private var discoveringStarted = false

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            currentEndpointId = endpointId
            Log.d(TAG, "onConnectionInitiated: Initiating connection with endpoint $endpointId")
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                _status.value = "Connected to $endpointId"
                Log.d(TAG, "onConnectionResult: Connected to endpoint $endpointId")
                messageToSend?.let {
                    sendDebugMessage(endpointId, it)
                }
            } else {
                _status.value = "Connection failed"
                Log.d(TAG, "onConnectionResult: Connection to endpoint $endpointId failed")
            }
        }

        override fun onDisconnected(endpointId: String) {
            _status.value = "Disconnected from $endpointId"
            currentEndpointId = null
            stopAdvertising()
            stopDiscovery()
            Log.d(TAG, "onDisconnected: Disconnected from endpoint $endpointId")
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        private var receivedBytes = ByteArrayOutputStream()
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES && false) {
                val debugMessage = String(payload.asBytes()!!)
                _receivedDebugMessage.value = debugMessage
                Log.d(TAG, "onPayloadReceived: Received payload from $endpointId: $debugMessage")
            }
            if (payload.type == Payload.Type.STREAM) {
                Log.d(TAG, "Received stream payload from $endpointId")

                val inputStream = payload.asStream()?.asInputStream()
                inputStream?.let {
                    try {
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        val outputStream = ByteArrayOutputStream()

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }

                        receivedBytes.write(outputStream.toByteArray())

                        // Check if inputStream.read() returns -1, indicating end of stream
                        if (bytesRead == -1) {
                            Log.d(TAG, "Received last chunk of stream from $endpointId")

                            val imageBytes = receivedBytes.toByteArray()
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            if (bitmap != null) {

                                Log.d(TAG, "Successfully decoded bitmap from $endpointId")
                                Log.d(TAG, "BITMAPPPPP PP P PP P PP P P PP  $bitmap")
                            } else {
                                Log.e(TAG, "Failed to decode byte array into Bitmap")
                            }

                            // Reset for next image
                            receivedBytes.reset()
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
            // Handle transfer updates if needed
            Log.d(TAG, "onPayloadTransferUpdate: Transfer update for endpoint $endpointId: $update")
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
            Log.d(TAG, "startAdvertising: Started advertising")
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
            Log.d(TAG, "stopAdvertising: Stopped advertising")
        }
    }

    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startDiscovery(
            getApplication<Application>().packageName, object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    connectionsClient.requestConnection("DeviceName", endpointId, connectionLifecycleCallback)
                    _endpoints.value = _endpoints.value + endpointId
                    Log.d(TAG, "onEndpointFound: Found endpoint $endpointId")
                }

                override fun onEndpointLost(endpointId: String) {
                    _endpoints.value = _endpoints.value - endpointId
                    Log.d(TAG, "onEndpointLost: Lost endpoint $endpointId")
                }
            }, discoveryOptions
        ).addOnSuccessListener {
            _status.value = "Discovering..."
            discoveringStarted = true
            Log.d(TAG, "startDiscovery: Started discovering")
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
            Log.d(TAG, "stopDiscovery: Stopped discovering")
        }
    }

    fun disconnect() {
        currentEndpointId?.let {
            connectionsClient.disconnectFromEndpoint(it)
            _status.value = "Disconnecting from $it..."
            stopAdvertising()
            stopDiscovery()
            Log.d(TAG, "disconnect: Disconnecting from endpoint $it")
        }
    }

    private fun sendDebugMessage(endpointId: String, plant: Plant?) {
        //val payload = Payload.fromBytes(plant.toString().toByteArray())
        //connectionsClient.sendPayload(endpointId, payload)
        //Log.d(TAG, "sendDebugMessage: Sent payload to $endpointId: $plant")
        sendImage(endpointId, plant)
    }
    private fun sendImage(endpointId: String, plant: Plant?) {
        val imageUri = plant?.imagePath // Assuming imagePath is of type Uri

        try {
            val inputStream = imageUri?.let {
                getApplication<Application>().contentResolver.openInputStream(it)
            }

            if (inputStream != null) {
                // Adjust buffer size as needed
                val bufferSize = 1024 * 1024 // 1MB buffer
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int

                // Read from input stream and send in chunks
                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    val payload = Payload.fromStream(ByteArrayInputStream(buffer, 0, bytesRead))
                    connectionsClient.sendPayload(endpointId, payload)
                }

                Log.d("NearbyViewModel", "Image sent successfully to $endpointId")
            } else {
                Log.d("NearbyViewModel", "Input stream is null")
            }

            inputStream?.close() // Close the InputStream
        } catch (e: Exception) {
            Log.e("NearbyViewModel", "Exception occurred while sending image: ${e.message}", e)
        }
    }
}