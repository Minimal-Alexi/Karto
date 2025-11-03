package com.example.mapapp

import android.app.Application
import androidx.room.Room
import com.example.mapapp.data.database.KartoDatabase
import com.example.mapapp.data.database.UserRepository

class KartoApplication : Application() {
    private val database by lazy { KartoDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
}