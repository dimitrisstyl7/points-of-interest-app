package dimstyl.pointsofinterest.ui.models

import dimstyl.pointsofinterest.data.PointOfInterestEntity

data class PointOfInterestUiModel(
    val id: Int? = null,
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val rating: String = "", // min value: 1, max value: 5
    val minRating: Int = 1,
    val maxRating: Int = 5,
    val photoUri: String? = null,
    val isFavorite: Boolean = false,
) {

    fun toEntity() = PointOfInterestEntity(
        id = id,
        title = title,
        category = category,
        description = description,
        longitude = longitude,
        latitude = latitude,
        rating = rating.toInt(),
        photoUri = photoUri,
        isFavorite = isFavorite
    )

    fun contentEquals(pointOfInterestUiModel: PointOfInterestUiModel): Boolean {
        return title == pointOfInterestUiModel.title &&
                category == pointOfInterestUiModel.category &&
                description == pointOfInterestUiModel.description &&
                rating == pointOfInterestUiModel.rating &&
                photoUri == pointOfInterestUiModel.photoUri &&
                isFavorite == pointOfInterestUiModel.isFavorite
    }

}