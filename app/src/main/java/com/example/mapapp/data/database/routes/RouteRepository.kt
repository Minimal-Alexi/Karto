package com.example.mapapp.data.database.routes

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val dao : RouteDao) {
    fun getAllRoutes(): Flow<List<RouteEntity>> = dao.getAllRoutes()
    fun getRoute(id: Int): Flow<RouteEntity?> = dao.getRouteById(id)

    suspend fun saveRoute(route: RouteEntity) {
        dao.insertRoute(route)
    }

    suspend fun deleteRoute(route: RouteEntity) {
        dao.deleteRoute(route)
    }
}