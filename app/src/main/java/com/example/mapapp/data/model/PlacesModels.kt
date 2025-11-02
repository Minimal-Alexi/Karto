package com.example.mapapp.data.model

import com.google.android.libraries.places.api.model.Place

data class PlacesRequest(
    val maxResultCount:Int = 10,
    val locationRestriction: LocationRestriction
)

data class PlacesResponse(
    val places:List<Place>?
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