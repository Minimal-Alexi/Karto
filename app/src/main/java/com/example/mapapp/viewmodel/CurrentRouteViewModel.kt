package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import com.example.mapapp.data.model.DisplayName
import com.example.mapapp.data.model.Place
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CurrentRouteViewModel(application: Application) : AndroidViewModel(application) {
    private var _isOnRoute = MutableStateFlow(true) // TODO: get from database
    val isOnRoute: StateFlow<Boolean> = _isOnRoute

    // TODO: get from database
    private var _routeStops = MutableStateFlow<List<Place>>(listOf(
        Place(
            "fakeID",
            displayName = DisplayName("placeholder location"),
            location = LatLng(0.0, 0.0),
        ),

        Place(
            "fakeID",
            displayName = DisplayName("second placeholder location"),
            location = LatLng(0.0, 0.0),
        ),
    ))
    val routeStops: StateFlow<List<Place>> = _routeStops
}