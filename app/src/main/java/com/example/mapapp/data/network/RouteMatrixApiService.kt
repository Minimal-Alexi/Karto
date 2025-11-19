package com.example.mapapp.data.network

import com.example.mapapp.data.model.*
import com.example.mapapp.utils.SecretsHolder

import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header


interface RouteMatrixApiService {
    @Headers("Content-Type: application/json")
    @POST("distanceMatrix/v2:computeRouteMatrix")
    suspend fun computeRouteMatrix(
        @Body request: RouteMatrixRequest,
        @Header("X-Goog-Api-Key") apiKey: String = SecretsHolder.apiKey!!,
        @Header("X-Goog-FieldMask") fieldMask: String = "",
    ): RouteMatrixResponse
}