package com.example.mapapp.data.model

data class PlaceDetailsResponse(
    val displayName : DisplayName,
    val rating: Double,
    val editorialSummary : EditorialSummary,
)

data class EditorialSummary(
    val text : String
)