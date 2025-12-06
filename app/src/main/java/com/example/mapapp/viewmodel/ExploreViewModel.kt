package com.example.mapapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.data.database.template_stops.TemplateStopEntity
import com.example.mapapp.data.database.templates.TemplateEntity
import com.example.mapapp.data.location.DefaultLocationClient
import com.example.mapapp.data.location.LocationClient
import com.example.mapapp.data.model.Circle
import com.example.mapapp.data.model.DisplayName
import com.example.mapapp.data.model.LatLngLiteral
import com.example.mapapp.data.model.LocationRestriction
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.PlacesRequest
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.data.model.RouteLocation
import com.example.mapapp.data.model.RoutesRequest
import com.example.mapapp.data.model.TravelModes
import com.example.mapapp.data.model.TypesOfPlaces
import com.example.mapapp.data.network.PlacesApi
import com.example.mapapp.data.model.RouteMatrixRequest
import com.example.mapapp.data.model.RouteMatrixResponse
import com.example.mapapp.data.model.WayPoint
import com.example.mapapp.data.network.RouteMatrixApi
import com.example.mapapp.utils.RouteGenerator
import com.example.mapapp.data.network.RoutesApi
import com.example.mapapp.utils.DialogData
import com.example.mapapp.utils.SecretsHolder
import com.example.mapapp.utils.TravelRoute
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList
import kotlin.random.Random

object ExploreViewModelParameterRepository {
    val _routeStops = MutableStateFlow<MutableList<Place>>(mutableStateListOf())
    val _routePolyline = MutableStateFlow<String?>(null)
    val _routeDistance = MutableStateFlow<Int?>(null)
    val _routeTime = MutableStateFlow<String?>(null)

    val _travelMode = MutableStateFlow<TravelModes>(TravelModes.WALK)
    val _userLocation = MutableStateFlow<LatLng?>(null)
    val _customLocation = MutableStateFlow<LatLng?>(null)
    val _customLocationText = MutableStateFlow<String>("")
}

class ExploreViewModel(application: Application) : AndroidViewModel(application) {
    private val routeRepository = (application as KartoApplication).routeRepository
    private val templateRepository = (application as KartoApplication).templateRepository
    var routeTitle = mutableStateOf("Default Title")

    val dialogDataState = mutableStateOf<DialogData?>(null)

    /*
    Nearby Places & Filtering Variables
    */
    private val _nearbyPlaces = MutableStateFlow<List<Place>?>(null)
    val nearbyPlaces: StateFlow<List<Place>?> = _nearbyPlaces
    private val _placeTypeSelection = MutableStateFlow<TypesOfPlaces>(TypesOfPlaces.RESTAURANTS)
    val placeTypeSelector: StateFlow<TypesOfPlaces> = _placeTypeSelection
    private val _distanceToPlaces = MutableStateFlow<Double>(1000.0)
    val distanceToPlaces: StateFlow<Double> = _distanceToPlaces

    /*
    User Location
    */
    // private val _userLocation = MutableStateFlow<LatLng?>(null)
    private val _userLocation = ExploreViewModelParameterRepository._userLocation
    val userLocation: StateFlow<LatLng?> = _userLocation
    private val _customLocation = ExploreViewModelParameterRepository._customLocation
    val customLocation: StateFlow<LatLng?> = _customLocation

    private val _customLocationText = ExploreViewModelParameterRepository._customLocationText
    val customLocationText: StateFlow<String> = _customLocationText


    /*
    Route and Polyline
     */
    // private val _travelMode = MutableStateFlow<TravelModes>(TravelModes.WALK)
    private val _travelMode = ExploreViewModelParameterRepository._travelMode
    val travelMode: StateFlow<TravelModes> = _travelMode

    // private val _routeStops = MutableStateFlow<List<Place>>(listOf())
    private val _routeStops = ExploreViewModelParameterRepository._routeStops
    val routeStops: StateFlow<MutableList<Place>> = _routeStops

    // private val _routePolyline = MutableStateFlow<String?>(null)
    private val _routePolyline = ExploreViewModelParameterRepository._routePolyline
    val routePolyline: StateFlow<String?> = _routePolyline

    private val _routeDistance = ExploreViewModelParameterRepository._routeDistance
    val routeDistance: StateFlow<Int?> = _routeDistance

    private val _routeTime = ExploreViewModelParameterRepository._routeTime
    val routeTime: StateFlow<String?> = _routeTime

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
                    if (customLocation.value == null) {
                        _userLocation.value = LatLng(location.latitude, location.longitude)
                    } else {
                        _userLocation.value = customLocation.value
                    }
                }
        }
    }

    fun loadTemplate(routeId: Int) {
        viewModelScope.launch {
            val routeWithStops = templateRepository.getTemplateWithStops(routeId)
            routeTitle.value = routeWithStops.route.title
            _routeStops.value = routeWithStops.stops.map { stop ->
                Place(
                    displayName = DisplayName(stop.name),
                    location = LatLng(stop.latitude, stop.longitude),
                    id = stop.placesId,
                    typeOfPlace = TypesOfPlaces.values().find { it.name == stop.typeOfPlace }
                )
            } as MutableList<Place>
            _placeTypeSelection.value =
                routeWithStops.route.category
                    ?.let { name -> TypesOfPlaces.values().find { it.name == name } }
                    ?: TypesOfPlaces.RESTAURANTS
            _userLocation.value = LatLng(
                routeWithStops.route.startingLatitude,
                routeWithStops.route.startingLongitude
            )
            _customLocation.value = LatLng(
                routeWithStops.route.startingLatitude,
                routeWithStops.route.startingLongitude
            )
            _customLocationText.value =
                "(placeholder) ${routeWithStops.route.startingLatitude} ${routeWithStops.route.startingLongitude}"
        }
    }

    fun generateItineraryForUser(placeType: TypesOfPlaces,range : Double, location: LatLng){
        viewModelScope.launch {
            resetRoute()
            Log.d(null,"Generating itinerary for: $placeType,$range,$location")
            _travelMode.value = TravelModes.WALK
            _distanceToPlaces.value = range
            _placeTypeSelection.value = placeType
            setOriginLocation(location)
            val places = getNearbyPlacesSuspend()
            val numberOfStops = Random.nextInt(4) + 1
            val selectedPlaces = places.take(numberOfStops)
            Log.d(null,"Selected places $selectedPlaces")
            if(!selectedPlaces.isEmpty()){
                selectedPlaces.forEach { it -> addRouteStop(it)}
                Log.d(null, "Route was generated: $selectedPlaces")
            }
            else{
                // TODO: Throw warning.
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

    fun setOriginLocation(location: LatLng) {
        _customLocation.value = location
        _userLocation.value = location
    }

    /** note: the marker may be updated with a slight delay before the location
     * client fetches the user location again (see init) */
    fun nullCustomLocation(){
        _customLocation.value = null
        _customLocationText.value = "Your Current Location"
    }

    fun setCustomLocationText(customLocationText: String) {
        _customLocationText.value = customLocationText
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
                        response.routes?.firstOrNull()?.distanceMeters,
                        response.routes?.firstOrNull()?.duration ?: "No route found"
                    )

                _routeDistance.value = routeInfoWalkOrDrive.first
                _routeTime.value = routeInfoWalkOrDrive.second

            } catch (e: Exception) {
                e.printStackTrace()
                _routePolyline.value = null
                _routeDistance.value = null
                _routeTime.value = null
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
                    sortedRouteStops.add(_routeStops.value[i - 1])
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

    fun saveRoute() {
        viewModelScope.launch {
            val template = TemplateEntity(
                title = routeTitle.value.ifBlank { "No name route" },
                savedAt  = System.currentTimeMillis(),
                startingLatitude = userLocation.value!!.latitude,
                startingLongitude = userLocation.value!!.longitude,
                category = placeTypeSelector.value.name
            )
            val stops = routeStops.value.mapIndexed { index, stop ->
                TemplateStopEntity(
                    templateId = 0, // it's a placeholder that's replaced by real id in routeRepository.saveRoute()
                    placesId = stop.id,
                    name = stop.displayName.text,
                    latitude = stop.location.latitude,
                    longitude = stop.location.longitude,
                    stayMinutes = 30,
                    position = index,
                    typeOfPlace = stop.typeOfPlace?.name
                )
            }
            templateRepository.saveTemplate(template, stops)
        }
    }

    fun updateSavedRoute(routeId: Int) {
        viewModelScope.launch {
            val existingRoute = templateRepository.getTemplateWithStops(routeId).route

            val updatedTemplate = existingRoute.copy(
                title = routeTitle.value.ifBlank { "No name route" },
                savedAt = System.currentTimeMillis(),
                startingLatitude = userLocation.value!!.latitude,
                startingLongitude = userLocation.value!!.longitude,
                category = placeTypeSelector.value.name
            )

            val stops = routeStops.value.mapIndexed { index, stop ->
                TemplateStopEntity(
                    templateId = routeId,
                    placesId = stop.id,
                    name = stop.displayName.text,
                    latitude = stop.location.latitude,
                    longitude = stop.location.longitude,
                    stayMinutes = 30,
                    position = index,
                    typeOfPlace = stop.typeOfPlace?.name
                )
            }

            templateRepository.updateTemplate(updatedTemplate, stops)
        }
    }

    fun startRoute() {
        viewModelScope.launch {
            val route = RouteEntity(
                title = routeTitle.value.ifBlank { "No name route" },
                startedAt = System.currentTimeMillis(),
                startingLatitude = userLocation.value!!.latitude,
                startingLongitude = userLocation.value!!.longitude
            )

            val stops = routeStops.value.mapIndexed { index, stop ->
                RouteStopEntity(
                    routeId = 0, // it's a placeholder that's replaced by real id in routeRepository.saveRoute()
                    placesId = stop.id,
                    name = stop.displayName.text,
                    latitude = stop.location.latitude,
                    longitude = stop.location.longitude,
                    stayMinutes = 30,
                    position = index,
                    typeOfPlace = stop.typeOfPlace?.name,
                    distanceTo = stop.travelDistance,
                    timeTo = stop.travelDuration
                )
            }

            routeRepository.startRoute(route, stops)
        }
    }

    suspend fun getNearbyPlacesSuspend(): List<Place> {
        return try {
            val userLoc = _userLocation.value ?: return emptyList()

            val placeRequest = PlacesRequest(
                includedTypes = _placeTypeSelection.value.places,
                locationRestriction = LocationRestriction(
                    Circle(userLoc, _distanceToPlaces.value)
                )
            )

            val apiKey = SecretsHolder.apiKey ?: return emptyList()
            val response = PlacesApi.service.getNearbyPlaces(
                placeRequest,
                apiKey,
                "places.displayName,places.location,places.id"
            )

            response.places.forEach { it.typeOfPlace = _placeTypeSelection.value }
            response.places
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun resetRoute() {
        routeTitle.value = "Default Title"
        _routeStops.value = emptyList()
        _travelMode.value = TravelModes.WALK
        _distanceToPlaces.value = 1000.0 // default distance
        _placeTypeSelection.value = TypesOfPlaces.RESTAURANTS // default type
        _nearbyPlaces.value = emptyList()
        _routePolyline.value = null
        _userLocation.value = null
        _customLocation.value = null
        _customLocationText.value = ""
        _routeTime.value = null
        _routeDistance.value = null
    }
}
