package com.example.mapapp.data.database.user

import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao : UserDao) {
    fun getUser() : Flow<UserEntity?> {
        return dao.getUser()
    }

    suspend fun upsertUser(user: UserEntity) {
        dao.upsertUser(user)
    }
}