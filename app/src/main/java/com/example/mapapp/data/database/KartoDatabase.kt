package com.example.mapapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mapapp.data.database.route_stops.RouteStopDao
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteDao
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.database.template_stops.TemplateStopDao
import com.example.mapapp.data.database.template_stops.TemplateStopEntity
import com.example.mapapp.data.database.templates.TemplateDao
import com.example.mapapp.data.database.templates.TemplateEntity
import com.example.mapapp.data.database.user.UserDao
import com.example.mapapp.data.database.user.UserEntity

import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        RouteEntity::class,
        RouteStopEntity::class,
        TemplateEntity::class,
        TemplateStopEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class KartoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao
    abstract fun routeStopDao(): RouteStopDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateStopDao(): TemplateStopDao

    companion object {
        @Volatile
        private var INSTANCE: KartoDatabase? = null

        fun getDatabase(context: Context): KartoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KartoDatabase::class.java,
                    "karto_database"
                )
                .fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert the default user when the database is created
                        ioScope.launch {
                            getDatabase(context).userDao().upsertUser(
                                UserEntity(
                                    id = UserEntity.SINGLETON_ID,
                                    firstName = "",
                                    lastName = "",
                                    darkThemePreferred = false,
                                    currentRouteId = null
                                )
                            )
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
        private val ioScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
    }
}