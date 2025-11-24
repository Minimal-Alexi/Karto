package com.example.mapapp.data.database.route_stops

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteStopDao {
    @Insert
    suspend fun insert(stop: RouteStopEntity)

    @Query("SELECT * FROM route_stops WHERE routeId = :routeId")
    suspend fun getStopsForRoute(routeId: Int): List<RouteStopEntity>

    @Query("DELETE FROM route_stops WHERE routeId = :routeId")
    suspend fun deleteStopsByRoute(routeId: Int)

    @Query("SELECT * FROM route_stops WHERE routeId = (SELECT id FROM routes WHERE status = 'CURRENT' LIMIT 1) ORDER BY position ASC")
    fun getStopsForCurrentRoute(): Flow<List<RouteStopEntity>>

    @Query("UPDATE route_stops SET isVisited = 1 WHERE id = :stopId")
    suspend fun markVisited(stopId: Int)

    @Query("UPDATE route_stops SET isVisited = 0 WHERE id = :stopId")
    suspend fun markUnvisited(stopId: Int)
}