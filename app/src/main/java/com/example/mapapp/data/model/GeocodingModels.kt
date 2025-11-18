package com.example.mapapp.data.model

import com.google.gson.annotations.SerializedName

data class ReverseGeocodingResponse(
    @SerializedName("results")
    val results: List<AddressResult>
)

data class AddressResult(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>
)

data class AddressComponent(
    @SerializedName("long_name")
    val longName: String,
    @SerializedName("short_name")
    val shortName: String,
    val types: List<String>
)
