package com.example.mapapp.data.model

import com.google.android.gms.maps.model.LatLng

data class PlacesRequest(
    val maxResultCount:Int = 10,
    val locationRestriction: LocationRestriction
)

data class PlacesResponse(
    val places:List<Place>
)

data class Place(
    val displayName:DisplayName,
    val location: LatLng
)

data class DisplayName(
    val text:String
)

data class LocationRestriction(
    val circle:Circle
)
data class Circle(
    val center: LatLng,
    val radius:Double = 1000.0
)