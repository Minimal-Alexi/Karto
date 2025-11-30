package com.example.mapapp.data.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    @ColumnInfo(name = "first_name") val firstName : String?,
    @ColumnInfo(name = "last_name") val lastName : String?,
    @ColumnInfo(name = "dark_theme_preferred") val darkThemePreferred : Boolean,
    @ColumnInfo(name = "current_route_id") val currentRouteId: Int?
) {
    companion object {
        const val SINGLETON_ID = 1 // make sure only one user exists
    }
}