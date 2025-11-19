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
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.model.TravelModes
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.utils.SecretsHolder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository

    /*
    Nearby Places & Filtering Variables
    */
    private val _nearbyPlaces = MutableStateFlow<List<Place>?>(null)
    val nearbyPlaces: StateFlow<List<Place>?> = _nearbyPlaces
    private val _placeTypeSelection = MutableStateFlow<TypesOfPlaces>(TypesOfPlaces.BEACHES)
    val placeTypeSelector: StateFlow<TypesOfPlaces> = _placeTypeSelection
    private val _distanceToPlaces = MutableStateFlow<Double>(1000.0)
    val distanceToPlaces: StateFlow<Double> = _distanceToPlaces

    /*
    User Location
    */
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    /*
    Route and Polyline
     */
    private val _travelMode = MutableStateFlow<TravelModes>(TravelModes.WALK)
    val travelMode: StateFlow<TravelModes> = _travelMode
    private val _routeStops = MutableStateFlow<List<Place>>(listOf())
    val routeStops: StateFlow<List<Place>> = _routeStops
    private val _routePolyline = MutableStateFlow<String?>(null)
    val routePolyline: StateFlow<String?> = _routePolyline

    private val _routeInfo = MutableStateFlow<String?>(null)
    val routeInfo: StateFlow<String?> = _routeInfo


    private val _LocationCallbackUpdate = 10000L
    private val locationClient: LocationClient =
        DefaultLocationClient(
            application,
            LocationServices.getFusedLocationProviderClient(application)
        )

    init {
        viewModelScope.launch {
            locationClient.getLocationUpdates(_LocationCallbackUpdate)
                .collect { location ->
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
        }
    }
    fun changeTravelMode(newTravelMode: TravelModes){
        _travelMode.value = newTravelMode
    }
    fun addRouteStop(placeToToggle: Place) {
        val currentList = _routeStops.value.toMutableList()
        if (!currentList.contains(placeToToggle)) {
            currentList.add(placeToToggle)
        }
        _routeStops.value = currentList
    }
    fun removeRouteStop(placeToRemove:Place){
        val currentList = _routeStops.value.toMutableList()
        currentList.remove(placeToRemove)
        _routeStops.value = currentList
    }

    fun changeDistanceToPlaces(newValue: Double){
        if(newValue >= 500 || newValue <= 10000) _distanceToPlaces.value = newValue
    }

    fun changePlaceType(newPlaceType: TypesOfPlaces) {
        _placeTypeSelection.value = newPlaceType
    }

    fun fetchRoute(origin: RouteLatLng, destination: RouteLatLng, travelMode: String = "WALK") {
        viewModelScope.launch {
            try {
                val request = RoutesRequest(
                    origin = RouteLocation(location = LatLngLiteral(latLng = origin)),
                    destination = RouteLocation(location = LatLngLiteral(latLng = destination)),
                    travelMode = travelMode
                )
                val response = RoutesApi.service.computeRoutes(
                    request,
                    fieldMask = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline"
                )
                val polyline = response.routes?.firstOrNull()?.polyline?.encodedPolyline
                _routePolyline.value = polyline

                val routeInfoWalkOrDrive =
                    Pair(
                        response.routes?.firstOrNull()?.distanceMeters ?: "No route found",
                        response.routes?.firstOrNull()?.duration ?: "No route found"
                    )

                _routeInfo.value =
                    "Distance: ${routeInfoWalkOrDrive.first} meters \nTime: ${routeInfoWalkOrDrive.second} seconds"
            } catch (e: Exception) {
                e.printStackTrace()
                _routePolyline.value = null
                _routeInfo.value = "No route found"

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
                                _userLocation.value!!,
                                _distanceToPlaces.value
                            )
                        )
                    )
                    val apiKey = SecretsHolder.apiKey
                    if (apiKey != null) {
                        val response = PlacesApi.service.getNearbyPlaces(
                            placeRequest,
                            apiKey,
                            "places.displayName,places.location,places.id"
                        )
                        response.places.forEach { it.typeOfPlace = _placeTypeSelection.value }
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

    fun saveRoute(title: String) {
        val routeEntity = RouteEntity(
            title = title,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            routeRepository.saveRoute(routeEntity)
        }
    }
}
