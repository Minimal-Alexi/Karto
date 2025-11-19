package com.example.mapapp.data.database.routes

import android.util.Log
import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import kotlinx.coroutines.flow.Flow

class RouteRepository(
    private val routeDao: RouteDao,
    private val routeStopDao: RouteStopDao
) {
    fun getSavedRoutes(): Flow<List<RouteWithStopCount>> = routeDao.getSavedRoutes()

    fun getCompletedRoutes(): Flow<List<RouteWithStopCount>> = routeDao.getCompletedRoutes()

    suspend fun getRouteWithStops(routeId: Int): RouteWithStops {
        val route = routeDao.getRouteById(routeId)
            ?: throw IllegalStateException("Route with id $routeId does not exist")
        val stops = routeStopDao.getStopsForRoute(routeId)
        return RouteWithStops(route, stops)}

    fun getCurrentRoute(): Flow<RouteEntity?> = routeDao.getCurrentRoute()

    fun getCurrentRouteStops(): Flow<List<RouteStopEntity>> {
        return routeStopDao.getStopsForCurrentRoute()
    }

    suspend fun completeRoute(routeId: Int) {
        routeDao.updateRouteStatus(routeId, RouteStatus.COMPLETED)
    }

    suspend fun startRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        // ensure only one CURRENT route can exist
        routeDao.deleteCurrent()

        val id = routeDao.insertRoute(route.copy(status = RouteStatus.CURRENT)).toInt()

        stops.forEach { stop ->
            routeStopDao.insert(
                stop.copy(routeId = id)
            )
        }
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