package dimstyl.pointsofinterest.ui.screens.main

import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel
import dimstyl.pointsofinterest.ui.navigation.NavItem
import dimstyl.pointsofinterest.ui.navigation.navItems

data class MainState(
    val currentNavItem: NavItem = navItems[0], // Discoveries screen is the starting destination
    val pointOfInterestUiModel: PointOfInterestUiModel = PointOfInterestUiModel(),
    val draftPointOfInterestUiModel: PointOfInterestUiModel = PointOfInterestUiModel(),
    val showNewDiscoveryDialog: Boolean = false,
    val showViewDiscoveryDialog: Boolean = false,
    val permissions: List<String> = listOf(),
    val processingPointOfInterest: Boolean = false
)