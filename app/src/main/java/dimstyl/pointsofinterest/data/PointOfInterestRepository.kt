package dimstyl.pointsofinterest.data

class PointOfInterestRepository(private val pointOfInterestDao: PointOfInterestDao) {

    fun pointsOfInterest() = pointOfInterestDao.getAll()
    fun getPointOfInterest(id: Int) = pointOfInterestDao.getById(id)
    suspend fun addPointOfInterest(wish: PointOfInterestEntity) = pointOfInterestDao.insert(wish)
    suspend fun updatePointOfInterest(wish: PointOfInterestEntity) = pointOfInterestDao.update(wish)
    suspend fun deletePointOfInterest(wish: PointOfInterestEntity) = pointOfInterestDao.delete(wish)

}