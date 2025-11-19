package com.example.mapapp.data.database.routes

import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import kotlinx.coroutines.flow.Flow

class RouteRepository(
    private val routeDao: RouteDao,
    private val routeStopDao: RouteStopDao
) {
    fun getAllRoutes(): Flow<List<RouteEntity>> = routeDao.getAllRoutes()
    suspend fun getRoute(id: Int): RouteEntity? = routeDao.getRouteById(id)

    fun getAllRoutesWithStopCount(): Flow<List<RouteWithStopCount>> =
        routeDao.getAllRoutesWithStopCount()

    fun getCurrentRoute(): Flow<RouteEntity?> = routeDao.getCurrentRoute()
    fun getSavedRoutes(): Flow<List<RouteEntity>> = routeDao.getSavedroutes()
    fun getCompletedRoutes(): Flow<List<RouteEntity>> = routeDao.getCompletedRoutes()

    suspend fun getRouteWithStops(routeId: Int): RouteWithStops {
        val route = routeDao.getRouteById(routeId)
            ?: throw IllegalStateException("Route with id $routeId does not exist")
        val stops = routeStopDao.getStopsForRoute(routeId)
        return RouteWithStops(route, stops)}

    suspend fun saveRoute(route: RouteEntity) {
        routeDao.insertRoute(route.copy(status = RouteStatus.SAVED))
    }

    suspend fun setCurrentRoute(route: RouteEntity) {
        routeDao.deleteCurrent()
        routeDao.insertRoute(route.copy(status = RouteStatus.CURRENT))
    }

    suspend fun completeRoute(route: RouteEntity) {
        routeDao.deleteCurrent()
        routeDao.insertRoute(route.copy(status = RouteStatus.COMPLETED))
    }

    suspend fun saveRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        val id = routeDao.insertRoute(route).toInt()

        stops.forEach { stop ->
            routeStopDao.insert(
                stop.copy(routeId = id)
            )
        }
    }

    suspend fun deleteRouteById(routeId: Int) {
        routeDao.deleteRouteById(routeId)
        routeStopDao.deleteStopsByRoute(routeId)
    }
}

data class RouteWithStops(
    val route: RouteEntity,
    val stops: List<RouteStopEntity> = emptyList()
)

data class RouteWithStopCount(
    val id: Int,
    val title: String,
    val savedAt: Long,
    val stopsCount: Int
)