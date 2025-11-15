package com.example.mapapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapapp.data.database.user.UserDao
import com.example.mapapp.data.database.user.UserEntity
import com.example.mapapp.data.database.routes.RouteDao
import com.example.mapapp.data.database.routes.RouteEntity

@Database(
    entities = [UserEntity::class, RouteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class KartoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao

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