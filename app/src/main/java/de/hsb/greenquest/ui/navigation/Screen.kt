package de.hsb.greenquest.ui.navigation

sealed class Screen(val route: String) {
    object PortfolioScreen: Screen("portfolio_screen")
    object PlantDetailScreen: Screen("plant_detail_screen")
    object CameraScreen: Screen("camera_screen")
    object ChallengeScreen: Screen("challenge_screen")
    object NearbyShareScreen: Screen("nearby_share_screen")

}