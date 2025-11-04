package com.example.mapapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaceViewModel(private val placesClient: PlacesClient) : ViewModel() {
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions = _predictions.asStateFlow()

    private val token = AutocompleteSessionToken.newInstance()

    fun searchPlaces(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        viewModelScope.launch {
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _predictions.value = response.autocompletePredictions
                }
                .addOnFailureListener { _predictions.value = emptyList() }
        }
    }
}
