package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.model.LatLngLiteral
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.data.model.RouteLocation
import com.example.mapapp.data.model.RoutesRequest
import com.example.mapapp.data.network.RoutesApi
import com.example.mapapp.model.location.DefaultLocationClient
import com.example.mapapp.model.location.LocationClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    /*
    Route and Polyline
     */
    private val _routePolyline = MutableStateFlow<String?>(null)
    val routePolyline: StateFlow<String?> = _routePolyline


    private val locationClient: LocationClient =
        DefaultLocationClient(
            application,
            LocationServices.getFusedLocationProviderClient(application)
        )

    init {
        viewModelScope.launch {
            locationClient.getLocationUpdates(10000L)
                .collect { location ->
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
        }
    }


    fun fetchRoute(apiKey: String, origin: RouteLatLng, destination: RouteLatLng) {
        viewModelScope.launch {
            try {
                val request = RoutesRequest(
                    origin = RouteLocation(location = LatLngLiteral(latLng = origin)),
                    destination = RouteLocation(location = LatLngLiteral(latLng = destination))
                )
                val response = RoutesApi.service.computeRoutes(request, apiKey, "routes.polyline.encodedPolyline")
                val polyline = response.routes?.firstOrNull()?.polyline?.encodedPolyline
                _routePolyline.value = polyline
            } catch (e: Exception) {
                e.printStackTrace()
                _routePolyline.value = null
            }
        }
    }
}
