package com.example.mapapp.data.database.routes

import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.user.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


class RouteRepository(
    private val routeDao: RouteDao,
    private val routeStopDao: RouteStopDao,
    private val userDao: UserDao
) {
    fun getAllRoutes(): Flow<List<RouteWithStopCount>> = routeDao.getAllRoutes()

    suspend fun getRouteWithStops(routeId: Int): RouteWithStops {
        val route = routeDao.getRouteById(routeId)
            ?: throw IllegalStateException("Route with id $routeId does not exist")
        val stops = routeStopDao.getStopsForRoute(routeId)
        return RouteWithStops(route, stops)}

    fun getCurrentRoute(): Flow<RouteEntity?> = routeDao.getCurrentRoute()

    fun getCurrentRouteStops(): Flow<List<RouteStopEntity>> =
        routeDao.getCurrentRoute().flatMapLatest { route ->
            if (route == null) flowOf(emptyList())
            else routeStopDao.getStopsForRoute(route.id)
        }

    suspend fun completeRoute(routeId: Int, timestamp: Long = System.currentTimeMillis()) {
        routeDao.completeRoute(routeId, timestamp)
    }

    suspend fun startRoute(route: RouteEntity, stops: List<RouteStopEntity>) {
        val id = routeDao.insertRoute(route.copy()).toInt()

        stops.forEach { stop ->
            routeStopDao.insert(
                stop.copy(routeId = id)
            )
        }
        userDao.setCurrentRoute(id)
    }

    suspend fun deleteRouteById(routeId: Int) {
        routeDao.deleteRouteById(routeId)
        routeStopDao.deleteStopsByRoute(routeId)
    }

    suspend fun updateRouteStop(routeStopEntity: RouteStopEntity){
        routeStopDao.updateRouteStop(routeStopEntity)
    }
}

data class RouteWithStops(
    val route: RouteEntity,
    val stops: Flow<List<RouteStopEntity>> = flowOf(emptyList())
)

data class RouteWithStopCount(
    val id: Int,
    val title: String,
    val startedAt: Long,
    val completedAt: Long?,
    val stopsCount: Int?
)