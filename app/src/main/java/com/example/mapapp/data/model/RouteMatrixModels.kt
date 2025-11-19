package com.example.mapapp.data.model

data class RouteMatrixRequest(
    val origins: RouteMatrixOrigins,
    val destinations: String,
    val travelMode: String,
)

data class RouteMatrixOrigins(
    val waypoints: List<RouteLocation>
)

data class RouteMatrixDestinations(
    val waypoints: List<RouteLocation>
)

data class RouteMatrixResponse(
    val rows: List<RouteMatrixRow>
)

data class RouteMatrixRow(
    val originIndex: Int,
    val destinationIndex: Int,
    val status: String,
    val distanceMeters: String,
    val duration: String,
    val condition: String
)






