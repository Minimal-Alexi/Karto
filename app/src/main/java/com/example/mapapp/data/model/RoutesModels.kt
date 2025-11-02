package com.example.mapapp.data.model

data class RoutesRequest (
    val origin: Location,
    val destination: Location,
    val travelMode: String = "WALK",
)

data class Location(
    val latLng: LatLng,
)

data class LatLng(
    val latitude: Double,
    val longitude: Double,
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