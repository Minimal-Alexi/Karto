package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.database.user.UserEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val userRepository = (app as KartoApplication).userRepository
    private val routeRepository = (app as KartoApplication).routeRepository

    val completedRoutes: StateFlow<List<RouteEntity>> = routeRepository.getCompletedRoutes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val user: StateFlow<UserEntity?> = userRepository.getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun updateName(firstName: String, lastName: String) {
        viewModelScope.launch {
            val currentUser = userRepository.getUser().first()

            if (currentUser != null) {
                userRepository.upsertUser(
                    currentUser.copy(
                        firstName = firstName,
                        lastName = lastName
                    )
                )
            } else {
                createNew()
            }
        }
    }

    fun createNew() {
        viewModelScope.launch {
            userRepository.upsertUser(
                UserEntity(
                    firstName = "",
                    lastName = "",
                    darkThemePreferred = false
                )
            )
        }
    }

    fun deleteRoute(route: RouteEntity) {
        viewModelScope.launch {
            routeRepository.deleteRouteById(route.id)
        }
    }
}