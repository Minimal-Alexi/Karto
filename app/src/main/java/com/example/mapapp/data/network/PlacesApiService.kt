package com.example.mapapp.data.network

import com.example.mapapp.data.model.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PlacesApiService {
    @Headers("Content-Type: application/json")
    @POST("/v1/places:searchNearby")
    suspend fun computeRoutes(
        @Body request: PlacesRequest,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String
    ): PlacesResponse
}