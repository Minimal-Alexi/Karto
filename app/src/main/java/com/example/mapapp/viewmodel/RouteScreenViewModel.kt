package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.model.DisplayName
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.TypesOfPlaces
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository

    val currentRoute: StateFlow<RouteEntity?> = routeRepository.getCurrentRoute()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val isOnRoute : StateFlow<Boolean> = currentRoute
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
            title = currentRoute.value?.title ?: "Default Route Title",
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            routeRepository.completeRoute(routeEntity)
        }
    }
}
