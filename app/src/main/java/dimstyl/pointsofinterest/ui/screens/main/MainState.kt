package dimstyl.pointsofinterest.ui.screens.main

import dimstyl.pointsofinterest.ui.navigation.NavItem
import dimstyl.pointsofinterest.ui.navigation.navItems
import dimstyl.pointsofinterest.ui.screens.main.PointOfInterestUiModel

data class MainState(
    val currentNavItem: NavItem = navItems[0], // Places screen is the starting destination
    val pointOfInterestUiModel: PointOfInterestUiModel = PointOfInterestUiModel(),
    val showNewPlaceDialog: Boolean = false,
    val permissions: List<String> = listOf()
)