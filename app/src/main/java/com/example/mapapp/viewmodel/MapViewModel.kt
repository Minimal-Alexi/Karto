package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Center
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.CredentialManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    /*
    Nearby Places
    */
    private val _nearbyPlaces = MutableStateFlow<List<Place>?>(null)
    val nearbyPlaces: StateFlow<List<Place>?> = _nearbyPlaces


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
        getNearbyPlaces()
    }
    fun getNearbyPlaces(){
        viewModelScope.launch {
            try{
                if(_userLocation.value != null){
                    val placeRequest = PlacesRequest(
                        locationRestriction = LocationRestriction(
                            Circle(
                                Center(
                                    _userLocation.value!!.latitude,
                                    _userLocation.value!!.longitude
                                )
                            )
                        )
                    )
                    val response = PlacesApi.service.getNearbyPlaces(placeRequest,"TEMP" /*TODO*/,"places.displayName")
                    _nearbyPlaces.value = response.places
                    Log.d(null,_nearbyPlaces.value.toString())
                }
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
}
