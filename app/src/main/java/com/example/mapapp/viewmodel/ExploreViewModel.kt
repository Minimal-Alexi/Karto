package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.model.LatLngLiteral
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.data.model.RouteLocation
import com.example.mapapp.data.model.RoutesRequest
import com.example.mapapp.data.network.RoutesApi
import android.util.Log
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.model.TravelModes
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.model.RouteMatrixRequest
import com.example.mapapp.data.model.RouteMatrixResponse
import com.example.mapapp.data.model.WayPoint
import com.example.mapapp.data.network.RouteMatrixApi
import com.example.mapapp.utils.RouteGenerator
import com.example.mapapp.utils.SecretsHolder
import com.example.mapapp.utils.TravelRoute
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ExploreViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository

    /*
    Nearby Places & Filtering Variables
    */
    private val _nearbyPlaces = MutableStateFlow<List<Place>?>(null)
    val nearbyPlaces: StateFlow<List<Place>?> = _nearbyPlaces
    private val _placeTypeSelection = MutableStateFlow<TypesOfPlaces>(TypesOfPlaces.BEACHES)
    val placeTypeSelector: StateFlow<TypesOfPlaces> = _placeTypeSelection
    private val _distanceToPlaces = MutableStateFlow<Double>(1000.0)
    val distanceToPlaces: StateFlow<Double> = _distanceToPlaces

    /*
    User Location
    */
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    /*
    Route and Polyline
     */
    private val _travelMode = MutableStateFlow<TravelModes>(TravelModes.WALK)
    val travelMode: StateFlow<TravelModes> = _travelMode
    private val _routeStops = MutableStateFlow<List<Place>>(listOf())
    val routeStops: StateFlow<List<Place>> = _routeStops
    private val _routePolyline = MutableStateFlow<String?>(null)
    val routePolyline: StateFlow<String?> = _routePolyline

    private val _routeInfo = MutableStateFlow<String?>(null)
    val routeInfo: StateFlow<String?> = _routeInfo

    private val _LocationCallbackUpdate = 10000L
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

    fun changeTravelMode(newTravelMode: TravelModes) {
        _travelMode.value = newTravelMode
    }

    fun addRouteStop(placeToToggle: Place) {
        val currentList = _routeStops.value.toMutableList()
        if (!currentList.contains(placeToToggle)) {
            currentList.add(placeToToggle)
        }
        _routeStops.value = currentList
    }

    fun removeRouteStop(placeToRemove: Place) {
        val currentList = _routeStops.value.toMutableList()
        currentList.remove(placeToRemove)
        _routeStops.value = currentList
    }

    fun changeDistanceToPlaces(newValue: Double) {
        if (newValue >= 500 || newValue <= 10000) _distanceToPlaces.value = newValue
    }

    fun changePlaceType(newPlaceType: TypesOfPlaces) {
        _placeTypeSelection.value = newPlaceType
    }

    /**
     * Code of route polyline is below
     */

    fun fetchRoute(
        origin: RouteLatLng,
        destination: RouteLatLng,
        intermediates: List<RouteLatLng> = listOf(),
        travelMode: String = "WALK",
    ) {
        viewModelScope.launch {
            try {
                val request = RoutesRequest(
                    origin = RouteLocation(location = LatLngLiteral(latLng = origin)),
                    destination = RouteLocation(location = LatLngLiteral(latLng = destination)),
                    intermediates = intermediates.map {
                        RouteLocation(
                            location = LatLngLiteral(
                                latLng = it
                            )
                        )
                    },
                    travelMode = travelMode
                )
                val response = RoutesApi.service.computeRoutes(
                    request,
                    fieldMask = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline"
                )
                val polyline = response.routes?.firstOrNull()?.polyline?.encodedPolyline
                _routePolyline.value = polyline

                val routeInfoWalkOrDrive =
                    Pair(
                        response.routes?.firstOrNull()?.distanceMeters ?: "No route found",
                        response.routes?.firstOrNull()?.duration ?: "No route found"
                    )

                _routeInfo.value =
                    "Distance: ${routeInfoWalkOrDrive.first} meters \nTime: ${routeInfoWalkOrDrive.second} seconds"
            } catch (e: Exception) {
                e.printStackTrace()
                _routePolyline.value = null
                _routeInfo.value = "No route found"

            }
        }
    }

    fun getWayPoints(): List<WayPoint> {
        val waypoints = mutableListOf<WayPoint>()
        for (place in _routeStops.value) {
            waypoints.add(
                WayPoint(
                    RouteLocation(
                        location = LatLngLiteral(
                            latLng = RouteLatLng(
                                place.location.latitude,
                                place.location.longitude
                            )
                        )
                    )
                )
            )
        }

        // Get the user location safely to avoid null pointer exception
        val currentLocation = _userLocation.value
        if (currentLocation != null) {
            waypoints.add(
                WayPoint(
                    RouteLocation(
                        location = LatLngLiteral(
                            latLng = RouteLatLng(
                                currentLocation.latitude,
                                currentLocation.longitude
                            )
                        )
                    )
                )
            )
        } else {
            Log.e("ExploreViewModel", "User location is NULL! Route Matrix will likely fail.")
        }
        return waypoints
    }

    private val _routeMatrixResponse = MutableStateFlow<RouteMatrixResponse?>(null)
    val routeMatrixResponse: StateFlow<RouteMatrixResponse?> = _routeMatrixResponse

    suspend fun fetchRouteMatrix() {
        Log.d("AAA", "Fetching route matrix...")

        try {
            val request = RouteMatrixRequest(
                origins = getWayPoints(),
                destinations = getWayPoints(),
                travelMode = _travelMode.value.mode
            )

            Log.d("AAA", "Route matrix request: $request")

            val responseList = RouteMatrixApi.service.computeRouteMatrix(
                request,
                fieldMask = "originIndex,destinationIndex,duration,distanceMeters,status,condition"
            )

            Log.d("AAA", "Route matrix response: $responseList")

            _routeMatrixResponse.value = RouteMatrixResponse(element = responseList)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private val _generatedRoute = MutableStateFlow<TravelRoute?>(null)
    val generatedRoute: StateFlow<TravelRoute?> = _generatedRoute

    fun getTravelCostMatrix(routeMatrixResponse: MutableStateFlow<RouteMatrixResponse?>) {
        val index = 1 + _routeStops.value.size
        val matrix: Array<Array<Int>> = Array(index) { Array(index) { 0 } }

        for (item in routeMatrixResponse.value!!.element) {
            matrix[item.originIndex][item.destinationIndex] = item.distanceMeters
        }

        for (item in matrix) {
            Log.d("AAA", item.contentToString())
        }
        val routeGenerator = RouteGenerator()
        val generateRouteGreedy = routeGenerator.generateRoute(matrix)
        _generatedRoute.value = generateRouteGreedy
    }


    fun runMatrixFlow() {
        viewModelScope.launch {
            if (_userLocation.value == null) {
                Log.e("AAA", "Aborting: User location is null. Please wait for GPS.")
            } else if (_routeStops.value.isEmpty()) {
                Log.w(
                    "AAA",
                    "Warning: No stops added, calculating matrix only for current location?"
                )

                _routePolyline.value = ""
            } else {
                fetchRouteMatrix()
                Log.d("AAA", "Matrix fetch success!")
                getTravelCostMatrix(_routeMatrixResponse)

                // Get polyline
                val origin: RouteLatLng =
                    _userLocation.value!!.let { RouteLatLng(it.latitude, it.longitude) }

                val sortedRouteStops: MutableList<Place> = mutableListOf()

                for (i in _generatedRoute.value!!.travelPath.drop(1)) {
                    sortedRouteStops.add(_routeStops.value[i-1])
                }
                _routeStops.value = sortedRouteStops

                val destinationPlace = _routeStops.value.last()

                val destination: RouteLatLng =
                    RouteLatLng(
                        destinationPlace.location.latitude,
                        destinationPlace.location.longitude
                    )

                val intermediate: MutableList<RouteLatLng> = mutableListOf()

                for (place in _routeStops.value) {
                    intermediate.add(RouteLatLng(place.location.latitude, place.location.longitude))
                }

                fetchRoute(origin, destination, intermediate)
            }

        }
    }


    /**
     * Code of route polyline is above
     */

    fun getNearbyPlaces() {
        viewModelScope.launch {
            try {
                if (_userLocation.value != null) {
                    val placeRequest = PlacesRequest(
                        includedTypes = _placeTypeSelection.value.places,
                        locationRestriction = LocationRestriction(
                            Circle(
                                _userLocation.value!!,
                                _distanceToPlaces.value
                            )
                        )
                    )
                    val apiKey = SecretsHolder.apiKey
                    if (apiKey != null) {
                        val response = PlacesApi.service.getNearbyPlaces(
                            placeRequest,
                            apiKey,
                            "places.displayName,places.location,places.id"
                        )
                        response.places.forEach { it.typeOfPlace = _placeTypeSelection.value }
                        _nearbyPlaces.value = response.places
                        Log.d(null, "PLACES: " + _nearbyPlaces.value.toString())
                    } else {
                        Log.w(null, "No API key provided, how is this even working?")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveRoute(title: String) {
        val routeEntity = RouteEntity(
            title = title,
            savedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            routeRepository.saveRoute(routeEntity)
        }
    }
}
