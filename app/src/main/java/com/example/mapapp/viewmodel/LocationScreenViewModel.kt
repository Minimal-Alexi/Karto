package com.example.mapapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.SecretsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class LocationScreenViewModel(application : Application) : AndroidViewModel(application) {
    data class LocationUIState(
        val placeID : String? = null,
        val displayName : String? = null,
        val rating : Double? = null,
        val summary : String? = null,
        val type : String? = null,
        val photo : Bitmap? = null
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
                        "displayName,rating,editorialSummary,primaryTypeDisplayName,photos"
                    )

                    var fetchedBitmap : Bitmap? = null

                    if (!response.photos.isNullOrEmpty()) {
                        val photoResponse : ResponseBody = PlacesApi.service.getPhoto(
                            photoName = response.photos[0].name,
                            apiKey = apiKey
                        )

                        fetchedBitmap = BitmapFactory.decodeStream(photoResponse.byteStream())
                    }

                    _uiState.value = _uiState.value.copy(placeID = placeID,
                        displayName = response.displayName?.text,
                        rating = response.rating,
                        summary = response.editorialSummary?.text,
                        type = response.primaryTypeDisplayName?.text,
                        photo = fetchedBitmap
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}