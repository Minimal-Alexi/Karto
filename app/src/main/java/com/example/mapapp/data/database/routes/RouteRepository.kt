package com.example.mapapp.data.database.routes

import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.user.UserDao
import kotlinx.coroutines.flow.Flow

class RouteRepository(
    private val routeDao: RouteDao,
    private val routeStopDao: RouteStopDao,
    private val userDao: UserDao
) {
    fun getSavedRoutes(): Flow<List<RouteWithStopCount>> = routeDao.getRoutesByStatus(RouteStatus.SAVED)

    fun getCompletedRoutes(): Flow<List<RouteWithStopCount>> = routeDao.getRoutesByStatus(RouteStatus.COMPLETED)

    suspend fun getRouteWithStops(routeId: Int): RouteWithStops {
        val route = routeDao.getRouteById(routeId)
            ?: throw IllegalStateException("Route with id $routeId does not exist")
        val stops = routeStopDao.getStopsForRoute(routeId)
        return RouteWithStops(route, stops)}

    fun getCurrentRoute(): Flow<RouteEntity?> = routeDao.getCurrentRoute()

    fun getCurrentRouteStops(): Flow<List<RouteStopEntity>> {
        return routeStopDao.getStopsForCurrentRoute()
    }
    suspend fun updateRouteStop(routeStopEntity: RouteStopEntity){
        routeStopDao.updateRouteStop(routeStopEntity)
    }

    suspend fun completeRoute(routeId: Int) {
        routeDao.updateRouteStatus(routeId, RouteStatus.COMPLETED)
    }

    suspend fun startRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        // TODO: put this behavior behind a disclaimer window
        val id = routeDao.insertRoute(route.copy(status = RouteStatus.DEFAULT)).toInt()

        stops.forEach { stop ->
            routeStopDao.insert(
                stop.copy(routeId = id)
            )
        }

        userDao.setCurrentRoute(id)
    }

    suspend fun saveRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        val id = routeDao.insertRoute(route.copy(status = RouteStatus.SAVED)).toInt()

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

    suspend fun updateRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        routeDao.updateRoute(route)
        routeStopDao.deleteStopsByRoute(route.id)
        stops.forEach { stop ->
            routeStopDao.insert(stop.copy(routeId = route.id))
        }
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