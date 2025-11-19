package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedViewModel(application : Application) : AndroidViewModel(application) {
    private val repository = (application as KartoApplication).routeRepository

    val allRoutes: StateFlow<List<RouteEntity>> = repository.getSavedRoutes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteRoute(route: RouteEntity) {
        viewModelScope.launch {
            repository.deleteRoute(route)
        }
    }
}