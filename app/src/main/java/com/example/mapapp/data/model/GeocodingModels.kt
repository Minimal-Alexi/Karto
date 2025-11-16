package com.example.mapapp.data.model

data class ReverseGeocodingResponse(
    val results: Array<AddressComponent>
)
data class AddressComponent(
    val longName: String,
    val shortName: String,
    val types: Array<String>
)