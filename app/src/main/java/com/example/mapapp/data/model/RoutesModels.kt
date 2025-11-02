package com.example.mapapp.data.model


data class RoutesRequest (
    val origin: RouteLocation,
    val destination: RouteLocation,
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
)

data class Polyline(
    val encodedPolyline: String?
)