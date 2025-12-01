package com.example.mapapp.data.database.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity): Long

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: Int): RouteEntity?

    @Query("""
        SELECT 
            r.id AS id,
            r.title AS title,
            r.started_at AS startedAt,
            r.completed_at AS completedAt,
            COUNT(s.id) AS stopsCount
        FROM routes r
        LEFT JOIN route_stops s ON s.route_id = r.id
        GROUP BY r.id
        ORDER BY r.started_at DESC
    """)
    fun getAllRoutes(): Flow<List<RouteWithStopCount>>

    @Query("""
        SELECT * FROM routes 
        WHERE id = (SELECT current_route_id FROM user WHERE id = 1)
    """)
    fun getCurrentRoute(): Flow<RouteEntity?>

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: Int)

    @Query("UPDATE routes SET completed_at = :timestamp WHERE id = :routeId")
    suspend fun completeRoute(routeId: Int, timestamp: Long = System.currentTimeMillis())
}