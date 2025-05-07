package dimstyl.pointsofinterest.data

class PointOfInterestRepository(private val pointOfInterestDao: PointOfInterestDao) {

    fun pointsOfInterest() = pointOfInterestDao.getAll()
    fun getPointOfInterest(id: Int) = pointOfInterestDao.getById(id)
    suspend fun addPointOfInterest(poi: PointOfInterestEntity) = pointOfInterestDao.insert(poi)
    suspend fun updatePointOfInterest(poi: PointOfInterestEntity) = pointOfInterestDao.update(poi)
    suspend fun deletePointOfInterest(poi: PointOfInterestEntity) = pointOfInterestDao.delete(poi)

}