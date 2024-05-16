package de.hsb.greenquest.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.hsb.greenquest.ui.theme.spacing

@Composable
fun CategoryScreen(
    navController: NavController,
    title: String = "",
    names: List<String> = listOf()
) {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        LazyVerticalGrid(columns = GridCells.Adaptive(116.dp), content = {
            items(names.size) { index ->
                //PortfolioElement(names[index])
            }
        })
    }

}
