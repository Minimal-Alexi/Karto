package com.example.mapapp.data.network

import com.example.mapapp.BuildConfig
import com.example.mapapp.data.model.*
import com.example.mapapp.utils.SecretsHolder

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RoutesApiService {
    @Headers("Content-Type: application/json")
    @POST("directions/v2:computeRoutes")
    suspend fun computeRoutes(
        @Body request: RoutesRequest,
        @Header("X-Goog-Api-Key") apiKey: String = SecretsHolder.apiKey!!,
        @Header("X-Goog-FieldMask") fieldMask: String = "",
    ): RoutesResponse
}