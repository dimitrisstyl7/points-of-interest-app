package dimstyl.pointsofinterest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Query("SELECT * FROM PointOfInterestEntity")
    fun getAll(): Flow<List<PointOfInterestEntity>>

    @Query("SELECT * FROM PointOfInterestEntity WHERE id = :id")
    fun getById(id: Int): Flow<PointOfInterestEntity>

    @Insert
    suspend fun insert(pointOfInterestEntity: PointOfInterestEntity)

    @Update
    suspend fun update(pointOfInterestEntity: PointOfInterestEntity)

    @Delete
    suspend fun delete(pointOfInterestEntity: PointOfInterestEntity)

}