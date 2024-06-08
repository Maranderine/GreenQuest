package de.hsb.greenquest.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object PortfolioScreen: Screen("portfolio_screen", "Portfolio")
    object PlantDetailScreen: Screen("plant_detail_screen", "Plant Detail")
    object CameraScreen: Screen("camera_screen", "Camera")
    object ChallengeScreen: Screen("challenge_screen", "Challenges")
}