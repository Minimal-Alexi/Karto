package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.database.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUIState(
    val firstName: String? = null,
    val lastName: String? = null,
    val darkThemePreferred: Boolean = false,
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val userRepository = (app as KartoApplication).userRepository
    private val routeRepository = (app as KartoApplication).routeRepository

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState

    val completedRoutes: StateFlow<List<RouteEntity>> = routeRepository.getCompletedRoutes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateName(firstName: String, lastName : String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(firstName = firstName, lastName = lastName)

            val currentUser = userRepository.getUser().first()

            if (currentUser != null) {
                userRepository.upsertUser(currentUser.copy(firstName = firstName, lastName = lastName))
            } else {
                createNew()
            }
        }
    }

    fun createNew() {
        viewModelScope.launch {
            userRepository.upsertUser(UserEntity(firstName = "", lastName = "", darkThemePreferred = false))
        }
    }

    init {
        viewModelScope.launch {
            userRepository.getUser().collectLatest { user ->
                if (user != null) {
                    _uiState.value = SettingsUIState(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        darkThemePreferred = user.darkThemePreferred,
                    )
                }
            }
        }
    }
}