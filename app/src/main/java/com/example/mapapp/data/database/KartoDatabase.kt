package com.example.mapapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteDao
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.database.user.UserDao
import com.example.mapapp.data.database.user.UserEntity

@Database(
    entities = [UserEntity::class, RouteEntity::class, RouteStopEntity::class],
    version = 4,
    exportSchema = false
)
abstract class KartoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao
    abstract fun routeStopDao(): RouteStopDao

    companion object {
        @Volatile
        private var INSTANCE: KartoDatabase? = null

        fun getDatabase(context: Context): KartoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KartoDatabase::class.java,
                    "karto_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}