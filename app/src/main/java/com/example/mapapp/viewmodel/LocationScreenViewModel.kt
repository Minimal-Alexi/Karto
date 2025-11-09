package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
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
        val summary : String? = null
    )

    private val _uiState = MutableStateFlow(LocationUIState())
    val uiState : StateFlow<LocationUIState> = _uiState

    fun getLocationInformation(placeID : String) {
        viewModelScope.launch {
            try {
                val apiKey = SecretsHolder.apiKey
                if (apiKey != null) {
                    val response = PlacesApi.service.getPlaceInformation(
                        placeID,
                        apiKey,
                        "displayName,rating,editorialSummary"
                    )
                    _uiState.value = _uiState.value.copy(placeID = placeID,
                        displayName = response.displayName.text,
                        rating = response.rating,
                        summary = response.editorialSummary.text)
                    Log.d("APPTAG", response.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* TODO: DELETE!!! */
    fun setUIStateWithoutFetch() {
        _uiState.value = LocationUIState("testFAKE_ID",
            "Cool Place",
            2.3,
            "This is the best place ever. This text could be quite long but it might also not be")
    }

    fun clearUIState() {
        _uiState.value = LocationUIState()
    }
}