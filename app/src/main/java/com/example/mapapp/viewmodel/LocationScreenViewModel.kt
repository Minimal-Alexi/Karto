package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.SecretsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationScreenViewModel(application : Application) : AndroidViewModel(application) {
    data class LocationUIState(
        val placeID : String? = null,
        val displayName : String? = null,
        val rating : Double? = null,
        val summary : String? = null,
        val type : String? = null
    )

    private val _uiState = MutableStateFlow(LocationUIState())
    val uiState : StateFlow<LocationUIState> = _uiState

    fun getLocationInformation(placeID : String) {
        if (placeID == _uiState.value.placeID) {
            return
        }

        viewModelScope.launch {
            try {
                val apiKey = SecretsHolder.apiKey
                if (apiKey != null) {
                    val response = PlacesApi.service.getPlaceInformation(
                        placeID,
                        apiKey,
                        "displayName,rating,editorialSummary,primaryTypeDisplayName"
                    )

                    _uiState.value = _uiState.value.copy(placeID = placeID,
                        displayName = response.displayName?.text,
                        rating = response.rating,
                        summary = response.editorialSummary?.text,
                        type = response.primaryTypeDisplayName?.text
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}