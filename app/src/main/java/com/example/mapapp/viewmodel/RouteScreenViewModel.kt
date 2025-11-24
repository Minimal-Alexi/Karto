package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    val currentStops : StateFlow<List<RouteStopEntity>?> = routeRepository.getCurrentRouteStops()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun completeRoute() {
        viewModelScope.launch {
            currentRoute.value?.let { routeRepository.completeRoute(it.id) }
        }
    }

    fun markRouteStopVisit(routeStopEntity: RouteStopEntity){
        viewModelScope.launch {
            currentStops.value?.let { stops ->
                if(stops.any{it.id == routeStopEntity.id}){
                    routeRepository.updateRouteStop(routeStopEntity.copy(
                        isVisited = !routeStopEntity.isVisited
                    ))
                }

            }
        }
    }
}
