package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PredictionViewModel(application : Application) : AndroidViewModel(application) {
    val placesClient: PlacesClient = Places.createClient(application)
    private val _originPredictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val originPredictions = _originPredictions.asStateFlow()
    private val token = AutocompleteSessionToken.newInstance()

    fun searchPlacesForOrigin(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        viewModelScope.launch {
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _originPredictions.value = response.autocompletePredictions
                }
                .addOnFailureListener { _originPredictions.value = emptyList() }
        }
    }

    fun clearPredictionsForOrigin() {
        _originPredictions.value = emptyList()
    }
}
