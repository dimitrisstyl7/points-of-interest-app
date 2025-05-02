package dstyl.pointsofinterest.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val title: String,
    @ColumnInfo val category: String,
    @ColumnInfo val description: String?,
    @ColumnInfo val longitude: Double,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val rating: Int,
    @ColumnInfo val photo: String?,
)