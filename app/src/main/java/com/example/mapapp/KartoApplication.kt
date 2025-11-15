package com.example.mapapp

import android.app.Application
import android.content.pm.PackageManager
import androidx.room.Room
import com.example.mapapp.data.database.KartoDatabase
import com.example.mapapp.data.database.user.UserRepository
import com.example.mapapp.data.database.routes.RouteRepository
import com.example.mapapp.utils.SecretsHolder

class KartoApplication : Application() {
    private val database by lazy { KartoDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val routeRepository by lazy { RouteRepository(database.routeDao()) }

    override fun onCreate() {
        super.onCreate()
        val appInfo = applicationContext.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
        SecretsHolder.init(apiKey)
    }

}