package com.example.mapapp.data.model


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.vector.ImageVector

enum class TravelModes(val mode: String, val icon: ImageVector){
    WALK("Walk", Icons.AutoMirrored.Filled.DirectionsWalk),
    // TRANSIT("Transit",Icons.Default.DirectionsTransit),
    DRIVE("Drive",Icons.Default.DirectionsCar)
}
data class RoutesRequest (
    val origin: RouteLocation,
    val destination: RouteLocation,
    val intermediates: List<RouteLocation>,
    val travelMode: String = "DRIVE",
)

data class RouteLocation(
    val location: LatLngLiteral,
)

data class LatLngLiteral(
    val latLng: RouteLatLng,
)

data class RouteLatLng(
    val latitude: Double,
    val longitude: Double
)

data class RoutesResponse(
    val routes: List<Route>?,
)

data class Route(
    val polyline: Polyline?,
    val distanceMeters: Int?,
    val duration: String?,
    val legs: List<Leg>?,
)

data class Leg(
    val distanceMeters: Int?,
    val duration: String?,
)

data class Polyline(
    val encodedPolyline: String?
)