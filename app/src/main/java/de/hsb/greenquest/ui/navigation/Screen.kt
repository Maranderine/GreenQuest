package de.hsb.greenquest.ui.navigation

import de.hsb.greenquest.R
import de.hsb.greenquest.ui.UiText

sealed class Screen(val route: String, val title: UiText) {
    object PortfolioScreen: Screen("portfolio_screen",
        UiText.StringResource(resId = R.string.portfolio)
    )
    object PlantDetailScreen: Screen("plant_detail_screen",
        UiText.StringResource(resId = R.string.plant_detail)
    )
    object CameraScreen: Screen("camera_screen",
        UiText.StringResource(resId = R.string.camera)
    )
    object ChallengeScreen: Screen("challenge_screen",
        UiText.StringResource(resId = R.string.challenges)
    )
    object SearchCardsScreen: Screen("search_cards_screen",
        UiText.StringResource(resId = R.string.searchcards)
    )
    object NearbyShareScreen: Screen("nearby_share_screen",
        UiText.StringResource(resId = R.string.nearbyshare)
    )
}