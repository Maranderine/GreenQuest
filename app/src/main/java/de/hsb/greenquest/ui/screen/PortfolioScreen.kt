package de.hsb.greenquest.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.hsb.greenquest.R
import de.hsb.greenquest.ui.theme.spacing

@Composable
fun PortfolioScreen() {
    Column(
        modifier = Modifier
            .padding(MaterialTheme.spacing.small)
            .fillMaxSize()
    ) {

        var test = mutableListOf<String>(
            "Rose",
            "Sunflower",
            "Cannabis",
            "Cat",
            "Dog"
        )

        //PortfolioCategory(title = "Recent", names = test)
        PortfolioCategory2(title = "Recent", names = test)
        PortfolioCategory2(title = "Favorite", names = test)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioCategory(title: String, names: List<String>) {
    var isExpanded by remember { mutableStateOf(false) }
    val categories = listOf<String>("Recent", "Favorite")
    var category by remember {
        mutableStateOf(categories[0])
    }

    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = {isExpanded = it},
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.spacing.small))
                .fillMaxWidth()
        ) {
            TextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                categories.forEachIndexed { index, categoryName ->
                    DropdownMenuItem(
                        text = {
                            Text(text = categoryName)
                        },
                        onClick = {
                            category = categoryName
                            isExpanded = false
                        }
                    )
                }
            }
        }

        LazyVerticalGrid(columns = GridCells.Adaptive(116.dp), content = {
            items(names.size) { index ->
                PortfolioElement(names[index])
            }
        })
    }
}

@Composable
fun PortfolioCategory2(title: String, names: List<String>) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    Text(text = title)
    LazyRow {
        items(names.size) { index ->
            PortfolioElement(names[index])
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
    PortfolioScreen()
}