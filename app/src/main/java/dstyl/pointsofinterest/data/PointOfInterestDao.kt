package dstyl.pointsofinterest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Query("SELECT * FROM PointOfInterest")
    fun getAll(): Flow<List<PointOfInterest>>

    @Query("SELECT * FROM PointOfInterest WHERE id = :id")
    fun getById(id: Int): Flow<PointOfInterest>

    @Insert
    suspend fun insert(pointOfInterest: PointOfInterest)

    @Update
    suspend fun update(pointOfInterest: PointOfInterest)

    @Delete
    suspend fun delete(pointOfInterest: PointOfInterest)

}