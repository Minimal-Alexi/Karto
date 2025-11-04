package com.example.mapapp.data.database

import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao : UserDao) {
    fun getUser() : Flow<User?> {
        return dao.getUser()
    }

    suspend fun upsertUser(user: User) {
        dao.upsertUser(user)
    }
}