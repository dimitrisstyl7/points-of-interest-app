package dimstyl.pointsofinterest.data

class PointOfInterestRepository(private val pointOfInterestDao: PointOfInterestDao) {

    fun pointsOfInterest() = pointOfInterestDao.getAll()
    fun getPointOfInterest(id: Int) = pointOfInterestDao.getById(id)
    suspend fun addPointOfInterest(wish: PointOfInterest) = pointOfInterestDao.insert(wish)
    suspend fun updatePointOfInterest(wish: PointOfInterest) = pointOfInterestDao.update(wish)
    suspend fun deletePointOfInterest(wish: PointOfInterest) = pointOfInterestDao.delete(wish)

}