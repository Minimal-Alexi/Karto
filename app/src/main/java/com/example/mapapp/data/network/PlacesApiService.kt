package com.example.mapapp.data.network

import com.example.mapapp.data.model.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PlacesApiService {
    @POST("/v1/places:searchNearby")
    suspend fun computeRoutes(
        @Body request: RoutesRequest,
        @Header("X-Goog-Api-Key") apiKey: String
    ): RoutesResponse
}