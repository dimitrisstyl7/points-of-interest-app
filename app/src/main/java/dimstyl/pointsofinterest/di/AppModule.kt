package dimstyl.pointsofinterest.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dimstyl.pointsofinterest.data.AppDatabase
import dimstyl.pointsofinterest.data.PointOfInterestDao
import dimstyl.pointsofinterest.data.PointOfInterestRepository

class AppModule(context: Context) {

    private val database by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "poi.db").build()
    }

    private val pointOfInterestDao: PointOfInterestDao by lazy {
        database.pointOfInterestDao()
    }

    val pointOfInterestRepository: PointOfInterestRepository by lazy {
        PointOfInterestRepository(pointOfInterestDao)
    }

    val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

}