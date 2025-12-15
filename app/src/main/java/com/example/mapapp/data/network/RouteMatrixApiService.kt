package com.example.mapapp.data.network

import com.example.mapapp.data.model.RouteMatrixElement
import com.example.mapapp.data.model.RouteMatrixRequest
import com.example.mapapp.utils.SecretsHolder
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface RouteMatrixApiService {
    @Headers("Content-Type: application/json")
    @POST("distanceMatrix/v2:computeRouteMatrix")
    suspend fun computeRouteMatrix(
        @Body request: RouteMatrixRequest,
        @Header("X-Goog-Api-Key") apiKey: String = SecretsHolder.apiKey!!,
        @Header("X-Goog-FieldMask") fieldMask: String = "",
    ): List<RouteMatrixElement>
}