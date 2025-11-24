package com.example.mapapp.data.database.route_stops

class RouteStopRepository(private val routeStopDao: RouteStopDao) {
    suspend fun visitStop(stopId: Int) {
        routeStopDao.markVisited(stopId)
    }

    suspend fun unvisitStop(stopId: Int) {
        routeStopDao.markUnvisited(stopId)
    }
}