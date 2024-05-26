package de.hsb.greenquest.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.Plant
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.theme.spacing
import de.hsb.greenquest.ui.viewmodel.PortfolioViewModel

@Composable
fun PortfolioScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(MaterialTheme.spacing.small)
            .fillMaxSize()
    ) {
        //ImageGalleryApp()
        val portfolioViewModel = hiltViewModel<PortfolioViewModel>()

        //PortfolioCategory(title = "Recent", names = test)
        val plantList by portfolioViewModel.plantListFlow.collectAsStateWithLifecycle()

//        LazyRow {
//            items(plantList.size) { index ->
//                PortfolioElement(plant = plantList[index])
//            }
//        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        LazyVerticalGrid(columns = GridCells.Adaptive(116.dp), content = {
            items(plantList.size) { index ->
                PortfolioElement(plantList[index], navController, portfolioViewModel)
            }
        })



    }
}

@Composable
fun PortfolioCategory(title: String, navController: NavController, portfolioViewModel: PortfolioViewModel) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    Text(
        text = title,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        modifier = Modifier
            .clickable {
                //navController.navigate(Screen.PortfolioCategoryScreen.route)
            }
    )
}

@Composable
fun PortfolioElement(plant: Plant, navController: NavController, portfolioViewModel: PortfolioViewModel) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = plant.imagePath),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(0.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable {
                    Toast
                        .makeText(context, plant.name, Toast.LENGTH_SHORT)
                        .show()

                    navController.navigate(Screen.PlantDetailScreen.route + "/" + plant.name)

                }

        )
        Text(
            text = plant.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    PortfolioScreen(rememberNavController())
}