package com.example.mapapp.data.database.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity): Long

    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("""
        SELECT r.id, r.title, r.saved_at AS savedAt, COUNT(s.id) AS stopsCount
        FROM routes r
        LEFT JOIN route_stops s ON r.id = s.routeId
        GROUP BY r.id
        ORDER BY r.saved_at DESC
    """)
    fun getAllRoutesWithStopCount(): Flow<List<RouteWithStopCount>>

    @Query("select * from routes where id = :routeId limit 1")
    suspend fun getRouteById(routeId: Int): RouteEntity?

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: Int)
}