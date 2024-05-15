package de.hsb.greenquest.ui.viewmodel

import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.Plant

class PortfolioViewModel(

): ViewModel() {
    var flowerNames = mutableListOf<String>(
        "Rose",
        "Sunflower",
        "Cannabis",
        "Cat",
        "Dog"
    )

    val categories = listOf<String>("Recent", "Favorite", )

}