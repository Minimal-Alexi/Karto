package com.example.mapapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int = SINGLETON_ID,
    @ColumnInfo(name = "first_name") val firstName : String?,
    @ColumnInfo(name = "last_name") val lastName : String?,
    @ColumnInfo(name = "dark_theme_preferred") val darkThemePreferred : Boolean
) {
    companion object {
        const val SINGLETON_ID = 1 // make sure only one user exists
    }
}