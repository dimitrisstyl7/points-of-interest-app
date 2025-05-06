package dimstyl.pointsofinterest.ui.screens.main

data class PointOfInterestUiModel(
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val rating: String = "", // min value: 1, max value: 5
    val minRating: Int = 1,
    val maxRating: Int = 5,
    val photoUri: String = "",
    val isFavorite: Boolean = false,
)