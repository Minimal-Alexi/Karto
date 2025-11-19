package com.example.mapapp.data.database.route_stops

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteStopDao {

    @Insert
    suspend fun insert(stop: RouteStopEntity)

    @Query("SELECT * FROM route_stops WHERE routeId = :routeId")
    suspend fun getStopsForRoute(routeId: Int): List<RouteStopEntity>

    @Query("DELETE FROM route_stops WHERE routeId = :routeId")
    suspend fun deleteStopsByRoute(routeId: Int)
}