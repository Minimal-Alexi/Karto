package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteWithStopCount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedViewModel(application : Application) : AndroidViewModel(application) {
    private val repository = (application as KartoApplication).routeRepository

    val savedRoutes: StateFlow<List<RouteWithStopCount>> = repository.getSavedRoutes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteRoute(routeId: Int) {
        viewModelScope.launch {
            repository.deleteRouteById(routeId)
        }
    }
}