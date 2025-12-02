package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.data.network.GeocodingApi
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.utils.GeoResult
import com.example.mapapp.utils.SecretsHolder
import com.example.mapapp.utils.extractCityAndCountryFlexible
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as KartoApplication).userRepository
    private val _LocationCallbackUpdate = 3600000L // Once every hour.

    /*
    Greeting Card Variables
    */
    private val _firstName = MutableStateFlow<String>("First name not found")
    val firstName : StateFlow<String> = _firstName
    private val _greetingLocation = MutableStateFlow<String>("Unknown")
    val greetingLocation : StateFlow<String> = _greetingLocation
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    /*
    Route Generation Setup
    */
    private val _placeTypeSelection = MutableStateFlow<TypesOfPlaces>(TypesOfPlaces.BEACHES)
    val placeTypeSelector: StateFlow<TypesOfPlaces> = _placeTypeSelection
    private val _distanceToPlaces = MutableStateFlow<Double>(1000.0)
    val distanceToPlaces: StateFlow<Double> = _distanceToPlaces

    /*
    Suggestion Card
    */
    private val _suggestionCardNumbers = MutableStateFlow<HashMap<TypesOfPlaces,Int>>(hashMapOf(
        TypesOfPlaces.BEACHES to 0,
        TypesOfPlaces.NATURAL_FEATURES to 0,
        TypesOfPlaces.RESTAURANTS to 0
    ))
    val suggestionCardNumber: StateFlow<HashMap<TypesOfPlaces,Int>> = _suggestionCardNumbers

    /*

    */
    private val _customLocation = MutableStateFlow<LatLng?>(null)
    val customLocation: StateFlow<LatLng?> = _customLocation
    val _customLocationText = MutableStateFlow<String>("")

    private val locationClient: LocationClient =
        DefaultLocationClient(
            application,
            LocationServices.getFusedLocationProviderClient(application)
        )

    init {
        viewModelScope.launch {
            locationClient.getLocationUpdates(_LocationCallbackUpdate)
                .collect { location ->
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
        }
    }
    fun changePlaceType(newPlaceType: TypesOfPlaces) {
        _placeTypeSelection.value = newPlaceType
    }

    fun changeDistanceToPlaces(newValue: Double){
        if(newValue >= 500 || newValue <= 10000) _distanceToPlaces.value = newValue
    }

    fun getName(){
        viewModelScope.launch {
            repository.getUser().collectLatest{ user ->
                if(user?.firstName !=null){
                    _firstName.value = "Hello, ${user.firstName}"
                }
            }
        }
    }
    fun getNumberOfNearbySuggestions(typeOfPlaceToFetch: TypesOfPlaces){
        viewModelScope.launch {
            try {
                if (_userLocation.value != null) {
                    val placeRequest = PlacesRequest(
                        includedTypes = typeOfPlaceToFetch.places,
                        locationRestriction = LocationRestriction(
                            Circle(
                                _userLocation.value!!,
                                10000.0
                            )
                        )
                    )
                    val apiKey = SecretsHolder.apiKey
                    val response = PlacesApi.service.getNearbyPlaces(
                        placeRequest,
                        apiKey!!,
                        "places.id"
                    )
                    _suggestionCardNumbers.value =
                        _suggestionCardNumbers.value.toMutableMap().apply {
                            this[typeOfPlaceToFetch] = response.places.size
                        } as HashMap<TypesOfPlaces, Int>
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getReverseGeocodedLocation(){
        viewModelScope.launch{
            try{
                if(_userLocation.value != null){
                    val latitude = _userLocation.value!!.latitude
                    val longitude = _userLocation.value!!.longitude
                    val response = GeocodingApi.service.reverseGeocode(
                        "$latitude,$longitude",
                        "locality|country",
                        SecretsHolder.apiKey!!)
                    val geoResult = extractCityAndCountryFlexible(response)
                    createGreeting(geoResult)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
    private fun createGreeting(geoResult: GeoResult){
        val stringBuilder = StringBuilder()
        if(geoResult.city != null){
            stringBuilder.append(geoResult.city + ", ")
        }
        if(geoResult.country != null){
            stringBuilder.append(geoResult.country)
        }
        _greetingLocation.value = stringBuilder.toString()
        Log.d(null,_greetingLocation.value)
    }
    fun nullCustomLocation(){
        _customLocation.value = null
        _customLocationText.value = "Your Current Location"
    }
    fun setCustomLocationText(customLocationText: String) {
        _customLocationText.value = customLocationText
    }
    fun setOriginLocation(location: LatLng) {
        _customLocation.value = location
    }
}