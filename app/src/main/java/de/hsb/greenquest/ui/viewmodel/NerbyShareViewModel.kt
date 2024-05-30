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
import androidx.lifecycle.AndroidViewModel


@HiltViewModel
class NearbyViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(application.applicationContext)
    private val strategy = Strategy.P2P_STAR

    private val _status = mutableStateOf("Idle")
    val status: State<String> = _status

    private val _endpoints = mutableStateOf(listOf<String>())
    val endpoints: State<List<String>> = _endpoints

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
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
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Handle received payload
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Handle transfer updates
        }
    }

    fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        connectionsClient.startAdvertising(
            "DeviceName", getApplication<Application>().packageName, connectionLifecycleCallback, advertisingOptions
        ).addOnSuccessListener {
            _status.value = "Advertising..."
        }.addOnFailureListener {
            _status.value = "Advertising failed"
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
        }.addOnFailureListener {
            _status.value = "Discovery failed"
        }
    }
}