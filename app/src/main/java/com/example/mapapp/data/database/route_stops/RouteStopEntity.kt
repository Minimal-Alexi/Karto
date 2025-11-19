package com.example.mapapp.data.database.route_stops

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_stops")
data class RouteStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeId: Int,
    val placesId: String,  // this is place unique identifier from the Places API
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayMinutes: Int,
    val position: Int,
    val isVisited: Boolean = false
)