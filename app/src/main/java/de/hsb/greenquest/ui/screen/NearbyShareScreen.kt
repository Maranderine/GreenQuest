import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.viewmodel.NearbyViewModel

@Composable
fun NearbyConnectionScreen(viewModel: NearbyViewModel = hiltViewModel<NearbyViewModel>()) {
    // State variables to hold UI state
    var status by remember { mutableStateOf("Idle") }
    var endpoints by remember { mutableStateOf(emptyList<String>()) }
    var receivedDebugMessage by remember { mutableStateOf<String?>(null) }
    var receivedImageData by remember { mutableStateOf<ByteArray?>(null) }
    var debugMessageToSend by remember { mutableStateOf("") }

    // Plant object to advertise
    val plant = remember {
        Plant(
            name = "Sample Plant",
            commonNames = listOf("Common Name 1", "Common Name 2"),
            species = "Sample Species",
            description = "This is a sample plant description.",
            imagePath = Uri.parse("content://path/to/image"), // Example URI, adjust as needed
            favorite = false // Set to true if debugging favorite functionality
        )
    }

    // Effect to update UI when status changes
    LaunchedEffect(viewModel.status) {
        status = viewModel.status.value
    }

    // Effect to update UI when endpoints change
    LaunchedEffect(viewModel.endpoints) {
        endpoints = viewModel.endpoints.value
    }

    // Effect to update UI when debug message is received
    LaunchedEffect(viewModel.receivedDebugMessage) {
        receivedDebugMessage = viewModel.receivedDebugMessage.value
    }

    // Effect to update UI when image data is received
    LaunchedEffect(viewModel.receivedImageData) {
        receivedImageData = viewModel.receivedImageData.value
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Status: $status")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.startAdvertising(plant)
            Log.d("NearbyConnectionScreen", "Start Advertising button clicked")
        }) {
            Text("Start Advertising")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.startDiscovery()
            Log.d("NearbyConnectionScreen", "Start Discovery button clicked")
        }) {
            Text("Start Discovery")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Discovered Endpoints:")
        for (endpoint in endpoints) {
            Text(endpoint)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Received Debug Message:")
        Text(receivedDebugMessage ?: "No debug message received")
        Spacer(modifier = Modifier.height(16.dp))

        receivedImageData?.let { imageData ->
            Text("Received Image Payload: ${imageData.size} bytes")
            // Replace with actual image display logic if needed
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = debugMessageToSend,
            onValueChange = { debugMessageToSend = it },
            label = { Text("Debug Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.disconnect()
            Log.d("NearbyConnectionScreen", "Disconnect button clicked")
        }) {
            Text("Disconnect")
        }
    }
}