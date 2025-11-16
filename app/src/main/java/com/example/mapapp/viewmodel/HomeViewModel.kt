package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val locationClient: LocationClient =
        DefaultLocationClient(
            application,
            LocationServices.getFusedLocationProviderClient(application)
        )

    /*TODO: Figure out how to not bomb the GeoCode API with thousands of requests.*/
//    init {
//        viewModelScope.launch {
//            locationClient.getLocationUpdates(10000L)
//                .collect { location ->
//                    _userLocation.value = LatLng(location.latitude, location.longitude)
//                }
//        }
//    }
}