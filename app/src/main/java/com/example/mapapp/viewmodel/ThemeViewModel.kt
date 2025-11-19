package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(app : Application) : AndroidViewModel(app) {
    private val repository = (app as KartoApplication).userRepository

    val isDarkTheme: StateFlow<Boolean> = repository.getUser()
        .map { user ->
            user?.darkThemePreferred ?: false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = app.resources.configuration.uiMode==33
        )

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            val currentUser = repository.getUser().first() ?: return@launch
            val updatedUser = currentUser.copy(darkThemePreferred = isDark)

            repository.upsertUser(updatedUser)
        }
    }

    companion object {
        val Factory: (Application) -> ViewModelProvider.Factory = { application ->
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ThemeViewModel(application) as T
                }
            }
        }
    }
}