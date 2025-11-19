package com.example.mapapp.viewmodel

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.model.DisplayName
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.TypesOfPlaces
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository

    private var _isOnRoute = MutableStateFlow(true) // TODO: get from database
    val isOnRoute: StateFlow<Boolean> = _isOnRoute

    private var _title = MutableStateFlow("route title") // TODO: get from database
    val title: StateFlow<String> = _title

    // TODO: get from database
    private var _routeStops = MutableStateFlow<List<Place>>(listOf(
        Place(
            TypesOfPlaces.NATURAL_FEATURES,
            "fakeID",
            displayName = DisplayName("placeholder location"),
            location = LatLng(0.0, 0.0),
        ),

        Place(
            TypesOfPlaces.NATURAL_FEATURES,
            "fakeID",
            displayName = DisplayName("second placeholder location"),
            location = LatLng(0.0, 0.0),
        ),
    ))
    val routeStops: StateFlow<List<Place>> = _routeStops

    fun completeRoute() {
        val routeEntity = RouteEntity(
            title = _title.value,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            routeRepository.completeRoute(routeEntity)
        }
    }
}
