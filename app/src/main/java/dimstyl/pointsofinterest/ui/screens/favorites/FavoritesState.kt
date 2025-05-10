package dimstyl.pointsofinterest.ui.screens.favorites

import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel

data class FavoritesState(val discoveries: List<PointOfInterestUiModel> = emptyList())