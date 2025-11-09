package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.SecretsHolder
import kotlinx.coroutines.launch

class LocationScreenViewModel(application : Application) : AndroidViewModel(application) {
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
                    Log.d("APPTAG", response.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}