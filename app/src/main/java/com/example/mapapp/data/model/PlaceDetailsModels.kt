package com.example.mapapp.data.model

data class PlaceDetailsResponse(
    val displayName : DisplayName,
    val rating: Double,
    val editorialSummary : EditorialSummary,
    val primaryTypeDisplayName : PrimaryTypeDisplayName,
)

data class EditorialSummary(
    val text : String
)

data class PrimaryTypeDisplayName (
    val text : String
)