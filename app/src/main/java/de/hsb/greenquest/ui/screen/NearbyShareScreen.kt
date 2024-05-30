import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hsb.greenquest.ui.viewmodel.NearbyViewModel

@Composable
fun NearbyConnectionScreen(viewModel: NearbyViewModel = hiltViewModel<NearbyViewModel>()) {
    val status by viewModel.status
    val endpoints by viewModel.endpoints

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Status: $status")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.startAdvertising() }) {
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
    }
}

