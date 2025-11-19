package com.example.mapapp.data.database.routes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id = :id")
    fun getRouteById(id: Int): Flow<RouteEntity?>

    @Query("SELECT * FROM routes WHERE status = :status")
    fun getSavedroutes(status: RouteStatus = RouteStatus.SAVED): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE status = :status")
    fun getCompletedRoutes(status: RouteStatus = RouteStatus.COMPLETED): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE status = :status")
    fun getCurrentRoute(status: RouteStatus = RouteStatus.CURRENT): Flow<RouteEntity?>

    @Query("DELETE FROM routes WHERE status = :status")
    suspend fun deleteCurrent(status: RouteStatus = RouteStatus.CURRENT)

    @Delete
    suspend fun deleteRoute(route: RouteEntity)
}