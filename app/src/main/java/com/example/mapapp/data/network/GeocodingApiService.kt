package com.example.mapapp.data.network

import com.example.mapapp.data.model.ReverseGeocodingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface GeocodingApiService {
    @GET("json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("result_type") resultType: String? = null,
        @Query("key") apiKey: String
    ): ReverseGeocodingResponse
}