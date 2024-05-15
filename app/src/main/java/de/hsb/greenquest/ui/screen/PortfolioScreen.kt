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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.hsb.greenquest.R
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
        val portfolioViewModel = viewModel<PortfolioViewModel>()


        //PortfolioCategory(title = "Recent", names = test)

        portfolioViewModel.categories.forEach { categoryName ->
            PortfolioCategory(
                title = categoryName,
                navController = navController,
                portfolioViewModel = portfolioViewModel
            )
        }


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
                navController.navigate(Screen.PortfolioCategoryScreen.route)
            }
    )
    LazyRow {
        items(portfolioViewModel.flowerNames.size) { index ->
            PortfolioElement(portfolioViewModel.flowerNames[index])
        }
    }
}

@Composable
fun PortfolioElement(plantName: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.plant),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable {
                    Toast
                        .makeText(context, plantName, Toast.LENGTH_SHORT)
                        .show()
                }

        )
        Text(text = plantName)
    }

}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    PortfolioScreen(rememberNavController())
}