package dimstyl.pointsofinterest.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel

@Entity
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo val title: String,
    @ColumnInfo val category: String,
    @ColumnInfo val description: String? = null,
    @ColumnInfo val longitude: Double,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val rating: Int,
    @ColumnInfo val photoUri: String? = null,
    @ColumnInfo val isFavorite: Boolean,
) {

    fun toUiModel() = PointOfInterestUiModel(
        id = id,
        title = title,
        category = category,
        description = description ?: "",
        longitude = longitude,
        latitude = latitude,
        rating = rating.toString(),
        photoUri = photoUri,
        isFavorite = isFavorite
    )

}