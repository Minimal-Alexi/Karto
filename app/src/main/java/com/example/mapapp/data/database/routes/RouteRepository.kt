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

    suspend fun getRouteWithStops(routeId: Int): RouteWithStops {
        val route = routeDao.getRouteById(routeId)
            ?: throw IllegalStateException("Route with id $routeId does not exist")
        val stops = routeStopDao.getStopsForRoute(routeId)
        return RouteWithStops(route, stops)
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