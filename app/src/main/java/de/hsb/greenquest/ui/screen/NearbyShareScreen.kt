import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.viewmodel.NearbyViewModel

@Composable
fun NearbyConnectionScreen(viewModel: NearbyViewModel = hiltViewModel<NearbyViewModel>()) {
    val status by viewModel.status
    val endpoints by viewModel.endpoints
    val receivedDebugMessage by viewModel.receivedDebugMessage // Collect received debug message
    val receivedImageData by viewModel.receivedImageData.collectAsState() // Collect received image data

    var debugMessageToSend by remember { mutableStateOf("") } // State to hold debug message to send
    val plant = Plant(
        name = "Sample Plant",
        commonNames = listOf("Common Name 1", "Common Name 2"),
        species = "Sample Species",
        description = "This is a sample plant description.",
        imagePath = Uri.parse("content://path/to/image"), // Example URI, adjust as needed
        favorite = false // Set to true if debugging favorite functionality
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Status: $status")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.startAdvertising(plant) }) {
            Text("Start Advertising")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.startDiscovery() }) {
            Text("Start Discovery")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Discovered Endpoints:")
        for (endpoint in endpoints) {
            Text(endpoint)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Received Debug Message:")
        Text(receivedDebugMessage ?: "No debug message received") // Display received debug message or a default message
        Spacer(modifier = Modifier.height(16.dp))

        // Display received image payload if available
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
        Button(onClick = { viewModel.disconnect() }) {
            Text("Disconnect")
        }
    }
}