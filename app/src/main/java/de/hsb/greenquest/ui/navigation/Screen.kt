package de.hsb.greenquest.ui.navigation

sealed class Screen(val route: String) {
    object PortfolioScreen: Screen("portfolio_screen")
    object PortfolioCategoryScreen: Screen("category_screen")
    object CameraScreen: Screen("camera_screen")
}