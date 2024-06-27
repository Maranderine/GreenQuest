import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.viewmodel.NearbyViewModel

@Composable
fun NearbyConnectionScreen(viewModel: NearbyViewModel = hiltViewModel<NearbyViewModel>()) {
    val status by viewModel.status
    val endpoints by viewModel.endpoints
    val receivedDebugMessage by viewModel.receivedDebugMessage // Access received debug message
    val receivedBitmap by viewModel.receivedImageBitmap // Access received bitmap


    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.status, status))

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { viewModel.startDiscovery() }, modifier = Modifier.fillMaxSize()) {
            Text(stringResource(R.string.start_discovery))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(viewModel.createPlantFromString(receivedDebugMessage).commonNames.toString())
        Spacer(modifier = Modifier.height(16.dp))
        if (receivedBitmap != null) {
            DisplayReceivedImage(receivedBitmap!!)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column{
                Text(stringResource(R.string.discovered_endpoints))
                for (endpoint in endpoints) {
                    Text(endpoint)
                }

            Button(
                onClick = { viewModel.disconnect()
                          viewModel.resetState()},
                modifier = Modifier.padding(16.dp)
            ) {
                Text(stringResource(R.string.disconnect_resetui))
            }
            }
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
            contentDescription = stringResource(R.string.received_image),
            modifier = Modifier.fillMaxSize()
        )
    }
}