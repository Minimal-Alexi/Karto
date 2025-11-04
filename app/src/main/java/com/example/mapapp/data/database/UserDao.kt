package com.example.mapapp.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mapapp.data.database.User.Companion.SINGLETON_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: Int = SINGLETON_ID): Flow<User?>

    @Upsert
    suspend fun upsertUser(user: User)
}