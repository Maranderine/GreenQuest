import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.viewmodel.NearbyViewModel

@Composable
fun NearbyConnectionScreen(viewModel: NearbyViewModel = hiltViewModel<NearbyViewModel>()) {
    val status by viewModel.status
    val endpoints by viewModel.endpoints
    val receivedDebugMessage by viewModel.receivedDebugMessage // Access received debug message
    val receivedBitmap by viewModel.receivedImageBitmap // Access received bitmap

    //var debugMessageToSend by remember { mutableStateOf("") } // State to hold debug message to

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Status: $status")
        Spacer(modifier = Modifier.height(16.dp))
        //Button(onClick = { viewModel.startAdvertising(plant) }) {
        //    Text("Start Advertising")
        //}
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
        Text(receivedDebugMessage) // Display received debug message
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.disconnect() }) {
            Text("Disconnect")
        }
        if (receivedBitmap != null) {
            DisplayReceivedImage(receivedBitmap!!)
        }
    }
}
@Composable
fun DisplayReceivedImage(imageBitmap: ImageBitmap) {
    Box(
        //modifier = Modifier.fillMaxSize()
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Received Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}