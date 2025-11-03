package com.example.mapapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User :: class], version = 1)
abstract class KartoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: KartoDatabase? = null

        fun getDatabase(context: Context): KartoDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, KartoDatabase::class.java, "karto_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}