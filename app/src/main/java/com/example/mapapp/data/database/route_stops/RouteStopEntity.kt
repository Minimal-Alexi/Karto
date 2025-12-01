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
    @ColumnInfo(name = "place_id") val placesId: String,  // this is place unique identifier from the Places API
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "stay_minutes") val stayMinutes: Int,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "is_visited") val isVisited: Boolean = false,
    @ColumnInfo(name = "type") val typeOfPlace: String?,
    @ColumnInfo(name = "distance_to") val distanceTo: Int?,
    @ColumnInfo(name = "time_to") val timeTo: String?
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
            travelDistance = distanceTo,
            travelDuration = timeTo
        )
    }
}