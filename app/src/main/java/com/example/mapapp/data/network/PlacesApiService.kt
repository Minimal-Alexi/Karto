package com.example.mapapp.data.network

import androidx.room.Query
import com.example.mapapp.data.model.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface PlacesApiService {
    @Headers("Content-Type: application/json")
    @POST("/v1/places:searchNearby")
    suspend fun getNearbyPlaces(
        @Body request: PlacesRequest,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String
    ): PlacesResponse

    @Headers("Content-Type: application/json")
    @GET("/v1/places/{placeId}")
    suspend fun getPlaceInformation(
        @Path("placeId") placeId: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String
    ): PlaceDetailsResponse

    @Headers("Content-Type: application/json")
    @GET("/v1/{photoName}/media?maxWidthPx=800")
    suspend fun getPhoto(
        @Path(value = "photoName", encoded = true) photoName: String,
        @Header("X-Goog-Api-Key") apiKey: String
    ): ResponseBody
}