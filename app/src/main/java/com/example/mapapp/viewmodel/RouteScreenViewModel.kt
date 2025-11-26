package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository
    private val routeStopRepository = (application as KartoApplication).routeStopRepository

    /*
    User Location
    */
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

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

    /*
    Route handling
    */
    val currentRoute: StateFlow<RouteEntity?> = routeRepository.getCurrentRoute()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val currentStops : StateFlow<List<RouteStopEntity>?> = routeRepository.getCurrentRouteStops()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun visitStop(id: Int) {
        viewModelScope.launch {
            routeStopRepository.visitStop(id)
        }
    }

    fun unvisitStop(id: Int) {
        viewModelScope.launch {
            routeStopRepository.unvisitStop(id)
        }
    }

    fun completeRoute() {
        viewModelScope.launch {
            currentRoute.value?.let { routeRepository.completeRoute(it.id) }
        }
    }

}
