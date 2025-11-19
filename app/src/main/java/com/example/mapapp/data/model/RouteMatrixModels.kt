package com.example.mapapp.data.model

data class RouteMatrixRequest(
    val origins: List<WayPoint>,
    val destinations: List<WayPoint>,
    val travelMode: String,
)

data class WayPoint(
    val waypoint: RouteLocation
)

data class RouteMatrixResponse(
    val element: List<RouteMatrixElement>
)

data class RouteMatrixElement(
    val originIndex: Int,
    val destinationIndex: Int,
    val status: String,
    val distanceMeters: Int,
    val duration: Int,
    val condition: String
)






