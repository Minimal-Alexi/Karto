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

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("""
        SELECT r.id, r.title, r.timestamp AS savedAt, COUNT(s.id) AS stopsCount
        FROM routes r
        LEFT JOIN route_stops s ON r.id = s.routeId
        WHERE status = :status
        GROUP BY r.id
        ORDER BY r.timestamp DESC
    """)
    fun getRoutesByStatus(status: RouteStatus): Flow<List<RouteWithStopCount>>

    @Query("SELECT * FROM routes WHERE status = :status")
    fun getCurrentRoute(status: RouteStatus = RouteStatus.CURRENT): Flow<RouteEntity?>

    @Query("DELETE FROM routes WHERE status = :status")
    suspend fun deleteCurrent(status: RouteStatus = RouteStatus.CURRENT)

    @Query("UPDATE routes SET status = :newStatus WHERE id = :routeId")
    suspend fun updateRouteStatus(routeId: Int, newStatus: RouteStatus = RouteStatus.COMPLETED)

    @Query("select * from routes where id = :routeId limit 1")
    suspend fun getRouteById(routeId: Int): RouteEntity?

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRouteById(routeId: Int)
}