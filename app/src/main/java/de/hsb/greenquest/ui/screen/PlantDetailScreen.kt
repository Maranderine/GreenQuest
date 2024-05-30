package de.hsb.greenquest.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.theme.spacing
import de.hsb.greenquest.ui.viewmodel.PortfolioViewModel

@Composable
fun PlantDetailScreen(navController: NavController, name: String?) {
    val portfolioViewModel = hiltViewModel<PortfolioViewModel>()

    val plants = portfolioViewModel.plantListFlow.collectAsState()
    val plant = plants.value.find { it.name == name }

    Column(
        modifier = Modifier
            .padding(MaterialTheme.spacing.small)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name?: "No Name")
            IconButton(onClick = {
                plant?.let {
                val updatedPlant = it.copy(favorite = !it.favorite)

                portfolioViewModel.updatePlant(updatedPlant)
            }
            }) {
                ToggleIconButton(favorite = plant?.favorite ?: false)
            }
        }
        Image(
            painter = rememberAsyncImagePainter(model = plant?.imagePath),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
        )
        Box(Modifier.fillMaxSize()) {
            IconButton(
                onClick = { portfolioViewModel.openTextFieldDialog = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                if (plant?.description == "") {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }else {
                    Icon(
                        imageVector = Icons.Filled.Edit ,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "Common Names: " + (plant?.commonNames.toString()  ?: ""),
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.extraSmall))
                Text(
                    text = "Species: " + (plant?.species  ?: "")
                )
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.extraSmall))
                if (plant?.description != "") {
                    Text(
                        text = "Notes: " + (plant?.description ?: "")
                    )
                }
            }
            IconButton(
                onClick = {
                    portfolioViewModel.openDeleteDialog = true
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete ,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        plant?.let {
            Dialogs(
                portfolioViewModel = portfolioViewModel,
                navController = navController,
                plant = plant
            )
        }

    }
}

@Composable
fun Dialogs(
    portfolioViewModel: PortfolioViewModel,
    navController: NavController,
    plant: Plant
) {
    when {
        portfolioViewModel.openDeleteDialog -> {
            AlertDialog(
                onDismissRequest = { portfolioViewModel.openDeleteDialog = false },
                onConfirmation = {
                    portfolioViewModel.openDeleteDialog = false
                    portfolioViewModel.deletePlant(plant)
                    navController.navigate(Screen.PortfolioScreen.route)
                },
                dialogTitle = "Delete Plant",
                dialogText = "Are you really sure that you want to delete this plant?",
                icon = Icons.Default.Warning
            )
        }
        portfolioViewModel.openTextFieldDialog -> {
            DialogWithTextInput(
                plant = plant,
                onDismissRequest = { portfolioViewModel.openTextFieldDialog = false },
                onConfirmation = {
                    portfolioViewModel.openTextFieldDialog = false
                    portfolioViewModel.updatePlant(plant.copy(description = it))
                },
                labelText = "Notes",
            )
        }
    }
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun DialogWithTextInput(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    labelText: String,
    plant: Plant
) {
    var text by remember { mutableStateOf(plant.description) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(labelText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation(text) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


@Composable
fun ToggleIconButton(favorite: Boolean) {
    if (favorite) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
    else{
        Icon(
            painter = painterResource(id = R.drawable.baseline_star_outline_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PlantDetailScreenPreview() {
    PlantDetailScreen(rememberNavController(), "Rose")
}
