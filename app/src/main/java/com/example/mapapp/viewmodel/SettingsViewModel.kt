package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUIState(
    val firstName: String? = null,
    val lastName: String? = null,
    val darkThemePreferred: Boolean = false
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = (app as KartoApplication).userRepository

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState


    fun updateName(firstName: String, lastName : String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(firstName = firstName, lastName = lastName)

            val currentUser = repository.getUser().first()

            if (currentUser != null) {
                repository.upsertUser(currentUser.copy(firstName = firstName, lastName = lastName))
            } else {
                createNew()
            }
        }
    }

    fun createNew() {
        viewModelScope.launch {
            repository.upsertUser(User(firstName = "", lastName = "", darkThemePreferred = false))
        }
    }

    init {
        viewModelScope.launch {
            repository.getUser().collectLatest { user ->
                if (user != null) {
                    _uiState.value = SettingsUIState(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        darkThemePreferred = user.darkThemePreferred
                    )
                }
            }
        }
    }
}