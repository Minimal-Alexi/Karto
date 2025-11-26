package com.example.mapapp.data.database.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: Int = UserEntity.SINGLETON_ID): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("UPDATE user SET current_route_id = :routeId WHERE id = 1")
    suspend fun setCurrentRoute(routeId: Int)
}