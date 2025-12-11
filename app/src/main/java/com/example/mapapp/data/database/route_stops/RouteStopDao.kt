package com.example.mapapp.data.database.route_stops

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface RouteStopDao {
    @Insert
    suspend fun insert(stop: RouteStopEntity)

    @Update
    suspend fun updateRouteStop(routeStop: RouteStopEntity)

    @Query("SELECT * FROM route_stops WHERE route_id = :routeId")
    fun getStopsForRoute(routeId: Int): Flow<List<RouteStopEntity>>

    @Query("DELETE FROM route_stops WHERE route_id = :routeId")
    suspend fun deleteStopsByRoute(routeId: Int)

    @Query("UPDATE route_stops SET is_visited = 1 WHERE id = :stopId")
    suspend fun markVisited(stopId: Int)

    @Query("UPDATE route_stops SET is_visited = 0 WHERE id = :stopId")
    suspend fun markUnvisited(stopId: Int)
}