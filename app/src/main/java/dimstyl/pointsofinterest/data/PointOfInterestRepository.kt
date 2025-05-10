package dimstyl.pointsofinterest.data

class PointOfInterestRepository(private val pointOfInterestDao: PointOfInterestDao) {

    fun getAll() = pointOfInterestDao.getAll()
    fun getFavorites() = pointOfInterestDao.getFavorites()
    suspend fun save(poi: PointOfInterestEntity) = pointOfInterestDao.insert(poi)
    suspend fun update(poi: PointOfInterestEntity) = pointOfInterestDao.update(poi)
    suspend fun delete(poi: PointOfInterestEntity) = pointOfInterestDao.delete(poi)

}