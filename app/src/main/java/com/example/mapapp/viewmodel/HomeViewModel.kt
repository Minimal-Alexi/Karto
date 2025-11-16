package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.network.GeocodingApi
import com.example.mapapp.utils.GeoResult
import com.example.mapapp.utils.SecretsHolder
import com.example.mapapp.utils.extractCityAndCountryFlexible
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _LocationCallbackUpdate = 3600000L // Once every hour.

    private val _greetingLocation = MutableStateFlow<String>("Unknown")
    val greetingLocation : StateFlow<String> = _greetingLocation

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation
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

    fun getReverseGeocodedLocation(){
        viewModelScope.launch{
            try{
                if(_userLocation.value != null){
                    val latitude = _userLocation.value!!.latitude
                    val longitude = _userLocation.value!!.longitude
                    val response = GeocodingApi.service.reverseGeocode(
                        "$latitude,$longitude",
                        "locality|country",
                        SecretsHolder.apiKey!!)
                    val geoResult = extractCityAndCountryFlexible(response)
                    createGreeting(geoResult)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
    private fun createGreeting(geoResult: GeoResult){
        Log.d(null,geoResult)
        val stringBuilder = StringBuilder()
        if(geoResult.city != null){
            stringBuilder.append(geoResult.city + ", ")
        }
        if(geoResult.country != null){
            stringBuilder.append(geoResult.country)
        }
        _greetingLocation.value = stringBuilder.toString()
        Log.d(null,_greetingLocation.value)
    }
}