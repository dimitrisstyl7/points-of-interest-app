package dimstyl.pointsofinterest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PointOfInterestEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pointOfInterestDao(): PointOfInterestDao

}