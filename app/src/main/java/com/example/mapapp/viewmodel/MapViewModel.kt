package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.model.LatLngLiteral
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.data.model.RouteLocation
import com.example.mapapp.data.model.RoutesRequest
import com.example.mapapp.data.network.RoutesApi
import android.util.Log
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Center
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.SecretsHolder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    /*
    Nearby Places
    */
    private val _nearbyPlaces = MutableStateFlow<List<Place>?>(null)
    val nearbyPlaces: StateFlow<List<Place>?> = _nearbyPlaces
    private val _placeTypeSelection = MutableStateFlow<TypesOfPlaces>(TypesOfPlaces.BEACHES)
    val placeTypeSelector: StateFlow<TypesOfPlaces> = _placeTypeSelection


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


    fun changePlaceType(newPlaceType : TypesOfPlaces){
        _placeTypeSelection.value = newPlaceType
    }
    fun fetchRoute(origin: RouteLatLng, destination: RouteLatLng) {
        viewModelScope.launch {
            try {
                val request = RoutesRequest(
                    origin = RouteLocation(location = LatLngLiteral(latLng = origin)),
                    destination = RouteLocation(location = LatLngLiteral(latLng = destination))
                )
                val response = RoutesApi.service.computeRoutes(
                    request,
                    fieldMask = "routes.polyline.encodedPolyline"
                )
                val polyline = response.routes?.firstOrNull()?.polyline?.encodedPolyline
                _routePolyline.value = polyline
            } catch (e: Exception) {
                e.printStackTrace()
                _routePolyline.value = null
            }
        }
    }
    fun getNearbyPlaces() {
        viewModelScope.launch {
            try {
                if (_userLocation.value != null) {
                    val placeRequest = PlacesRequest(
                        includedTypes = _placeTypeSelection.value.places,
                        locationRestriction = LocationRestriction(
                            Circle(
                                Center(
                                    _userLocation.value!!.latitude,
                                    _userLocation.value!!.longitude
                                )
                            )
                        )
                    )
                    val apiKey = SecretsHolder.apiKey
                    if (apiKey != null) {
                        val response = PlacesApi.service.getNearbyPlaces(
                            placeRequest,
                            apiKey,
                            "places.displayName,places.location"
                        )
                        _nearbyPlaces.value = response.places
                        Log.d(null, "PLACES: " + _nearbyPlaces.value.toString())
                    } else {
                        Log.w(null, "No API key provided, how is this even working?")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
