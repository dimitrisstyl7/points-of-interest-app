package dimstyl.pointsofinterest.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PointOfInterest::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun PointOfInterestDao(): PointOfInterestDao

}