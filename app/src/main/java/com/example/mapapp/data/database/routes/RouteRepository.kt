package com.example.mapapp.data.database.routes

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val dao : RouteDao) {
    fun getAllRoutes(): Flow<List<RouteEntity>> = dao.getAllRoutes()
    fun getRoute(id: Int): Flow<RouteEntity?> = dao.getRouteById(id)
    fun getCurrentRoute(): Flow<RouteEntity?> = dao.getCurrentRoute()
    fun getSavedRoutes(): Flow<List<RouteEntity>> = dao.getSavedroutes()
    fun getCompletedRoutes(): Flow<List<RouteEntity>> = dao.getCompletedRoutes()

    suspend fun saveRoute(route: RouteEntity) {
        dao.insertRoute(route.copy(status = RouteStatus.SAVED))
    }

    suspend fun setCurrentRoute(route: RouteEntity) {
        dao.deleteCurrent()
        dao.insertRoute(route.copy(status = RouteStatus.CURRENT))
    }

    suspend fun completeRoute(route: RouteEntity) {
        dao.insertRoute(route.copy(status = RouteStatus.COMPLETED))
    }

    suspend fun deleteRoute(route: RouteEntity) {
        dao.deleteRoute(route)
    }
}