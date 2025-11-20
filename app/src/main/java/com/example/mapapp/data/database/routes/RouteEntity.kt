package com.example.mapapp.data.database.routes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "status") val status: RouteStatus = RouteStatus.DEFAULT,
    @ColumnInfo(name = "timestamp") val timestamp : Long
)