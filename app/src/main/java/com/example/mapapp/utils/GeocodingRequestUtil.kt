package com.example.mapapp.utils

import com.example.mapapp.data.model.ReverseGeocodingResponse

data class GeoResult(
    val city: String?,
    val country: String?
)

fun extractCityAndCountryFlexible(response: ReverseGeocodingResponse): GeoResult {
    val components = response.results.toList()

    val city = sequenceOf(
        "locality",
        "postal_town",
        "administrative_area_level_2",
        "administrative_area_level_1"
    ).mapNotNull { type ->
        components.firstOrNull { it.types.contains(type) }?.longName
    }.firstOrNull()

    val country = components
        .firstOrNull { it.types.contains("country") }
        ?.longName

    return GeoResult(city, country)
}
