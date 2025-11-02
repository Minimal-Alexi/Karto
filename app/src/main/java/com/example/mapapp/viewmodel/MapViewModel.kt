package com.example.mapapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    private val locationClient: LocationClient =
        DefaultLocationClient(application, LocationServices.getFusedLocationProviderClient(application))

    init {
        viewModelScope.launch {
            locationClient.getLocationUpdates(10000L)
                .collect { location ->
                    _userLocation.value = LatLng(location.latitude,location.longitude)
                }
        }
    }
}
