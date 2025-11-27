package com.example.mapapp.data.database.route_stops

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mapapp.data.model.DisplayName
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.TypesOfPlaces
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "route_stops")
data class RouteStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "route_id") val routeId: Int,
    val placesId: String,  // this is place unique identifier from the Places API
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayMinutes: Int,
    val position: Int,
    val isVisited: Boolean = false,
    val typeOfPlace: String?
){
    fun toPlace(): Place{
        val typesOfPlaceEnum = TypesOfPlaces.entries.toTypedArray().firstOrNull{
            it.name.equals(typeOfPlace,true)
        }
        return Place(
            typeOfPlace = typesOfPlaceEnum,
            id = placesId,
            displayName = DisplayName(name),
            location = LatLng(latitude,longitude),
        )
    }
}