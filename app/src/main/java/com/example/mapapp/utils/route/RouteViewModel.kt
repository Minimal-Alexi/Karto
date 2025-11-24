package com.example.mapapp.utils.route

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.data.model.LatLngLiteral
import com.example.mapapp.data.model.Leg
import com.example.mapapp.data.model.Place
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.data.model.RouteLocation
import com.example.mapapp.data.model.RouteMatrixRequest
import com.example.mapapp.data.model.RouteMatrixResponse
import com.example.mapapp.data.model.RoutesRequest
import com.example.mapapp.data.model.WayPoint
import com.example.mapapp.data.network.RouteMatrixApi
import com.example.mapapp.data.network.RoutesApi
import com.example.mapapp.utils.RouteGenerator
import com.example.mapapp.utils.TravelRoute
import com.example.mapapp.viewmodel.ExploreViewModelParameterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteViewModel(application: Application) : AndroidViewModel(application) {
    private val _routePolyline = ExploreViewModelParameterRepository._routePolyline
    private val _routeInfo = ExploreViewModelParameterRepository._routeInfo
    private val _routeStops = ExploreViewModelParameterRepository._routeStops
    private val _travelMode = ExploreViewModelParameterRepository._travelMode
    private val _userLocation = ExploreViewModelParameterRepository._userLocation

    // TODO: duplicated parameters, highly possible to case bugs
    private val _routeStopsInfo = MutableStateFlow<List<Leg>>(listOf())

    suspend fun fetchRoute(
        origin: RouteLatLng,
        destination: RouteLatLng,
        intermediates: List<RouteLatLng> = listOf(),
        travelMode: String = "WALK",
    ) {

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
                fieldMask = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline,routes.legs.distanceMeters,routes.legs.duration"
            )

            _routeStopsInfo.value = response.routes?.firstOrNull()?.legs?.dropLast(1) ?: listOf()

            // Update the distance and duration of each route stop
            for (i in _routeStopsInfo.value.indices) {
                _routeStops.value[i].travelDistance = _routeStopsInfo.value[i].distanceMeters.toString()
                _routeStops.value[i].travelDuration = _routeStopsInfo.value[i].duration.toString()
            }

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

        Log.d("AAA", "Matrix: " + _routeStops.value)

        val matrix: Array<Array<Int>> = Array(index) { Array(index) { 0 } }

        for (item in routeMatrixResponse.value!!.element) {
            matrix[item.originIndex][item.destinationIndex] = item.distanceMeters
        }

        for (item in matrix) {
            Log.d("AAA", "Matrix: " + item.contentToString())
        }
        val routeGenerator = RouteGenerator()
        val generateRouteGreedy = routeGenerator.generateRoute(matrix)
        _generatedRoute.value = generateRouteGreedy
    }

    // sort all routes and fetch the polyline
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
                getTravelCostMatrix(_routeMatrixResponse)

                // Get polyline
                val origin: RouteLatLng =
                    _userLocation.value!!.let { RouteLatLng(it.latitude, it.longitude) }

                // Sort route stops based on the generated route
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

    // Just fetch the polyline
    fun runWithoutSorting(){
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
                // Get polyline
                val origin: RouteLatLng =
                    _userLocation.value!!.let { RouteLatLng(it.latitude, it.longitude) }

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
}


