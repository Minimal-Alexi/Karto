package com.example.mapapp.data.model

data class PlacesRequest(
    val maxResultCount:Int = 10,
    val locationRestriction: LocationRestriction
)

data class LocationRestriction(
    val circle:Circle
)
data class Circle(
    val center:Center,
    val radius:Double = 1000.0
)
data class Center(
    val latitude:Double,
    val longitude:Double
)