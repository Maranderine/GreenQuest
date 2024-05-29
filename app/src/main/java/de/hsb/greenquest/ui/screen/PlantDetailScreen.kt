package de.hsb.greenquest.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import de.hsb.greenquest.R
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
        Text(
            text = "Common Names: " + (plant?.commonNames.toString()  ?: "")
        )
        Text(
            text = "Species: " + (plant?.species  ?: "")
        )
        Text(
            text = "Description: " + (plant?.description ?: "")
        )

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
