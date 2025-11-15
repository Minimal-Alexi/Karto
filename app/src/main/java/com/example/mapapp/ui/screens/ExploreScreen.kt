package com.example.mapapp.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.ui.components.PrimaryButton
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.ui.components.route.TravelModeSelector

import com.example.mapapp.viewmodel.ExploreViewModel
import com.example.mapapp.viewmodel.PredictionViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.android.gms.maps.model.LatLng as GmsLatLng

@Composable
fun ExploreScreen(navigateToLocationScreen: (String) -> Unit,
                  exploreViewModel: ExploreViewModel = viewModel()) {

    var routeTitle by remember { mutableStateOf("Default Route Title") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = routeTitle,
                onValueChange = { routeTitle = it },
                label = { Text("Route Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        item { MapScreen(exploreViewModel) }
        item {
            SelectedStopsSection(
                navigateToLocationScreen,
                exploreViewModel::removeRouteStop,
                exploreViewModel.routeStops.collectAsState().value,
            )
        }
        item { RouteSummarySection(exploreViewModel) }
        item {
            PrimaryButton(
                text = "Start This Route",
                backgroundColor = MaterialTheme.colorScheme.secondary
            ) { /* TODO */ }
        }
        item {
            PrimaryButton(
                text = "Save This Route For Later",
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                val titleToSave = routeTitle.ifBlank { "No name route" }
                exploreViewModel.saveRoute(title = titleToSave)
            }
        }
        item {
            PrimaryButton(
                text = "Reset This Route",
                backgroundColor = MaterialTheme.colorScheme.error
            ) { /* TODO */ }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun MapScreen(exploreViewModel: ExploreViewModel) {

    val userLocation = exploreViewModel.userLocation.collectAsState()
    val nearbyLocations = exploreViewModel.nearbyPlaces.collectAsState()
    val helsinki = LatLng(60.1699, 24.9384)

    val polyline = exploreViewModel.routePolyline.collectAsState()

    /**
     * Code of route polyline is below
     */
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val predictionViewModel = remember { PredictionViewModel(placesClient) }

    var originText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    val originPredictions by predictionViewModel.originPredictions.collectAsState()
    val destinationPredictions by predictionViewModel.destinationPredictions.collectAsState()

    var origin by remember { mutableStateOf(RouteLatLng(60.1699, 24.9384)) }
    var destination by remember {
        mutableStateOf(RouteLatLng(0.0, 0.0))
    }

    val routeInfo by exploreViewModel.routeInfo.collectAsState()
    var selectedMode by remember { mutableStateOf("Walking") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            OutlinedTextField(
                value = originText,
                onValueChange = {
                    predictionViewModel.clearPredictionsForDestination()
                    originText = it
                    if (it.length > 2) predictionViewModel.searchPlacesForOrigin(it)
                },
                label = { Text("Origin") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        predictionViewModel.clearPredictionsForDestination()
                    }
            )
            originPredictions.forEach { prediction ->
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val placeId = prediction.placeId
                            val placeFields = listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME)
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    val place = response.place
                                    origin = RouteLatLng(
                                        place.location!!.latitude,
                                        place.location!!.longitude
                                    )
                                    originText = place.formattedAddress ?: place.displayName
                                            ?: "" // Update the textFiled
                                    predictionViewModel.clearPredictionsForOrigin() // Clear the predictions after selecting one
                                    exploreViewModel.fetchRoute(
                                        origin,
                                        destination
                                    ) // Update the Route polyline after select
                                }
                        }
                        .padding(8.dp)
                )
            }
        }

        // For testing now, remove later
        Column {
            OutlinedTextField(
                value = destinationText,
                onValueChange = {
                    predictionViewModel.clearPredictionsForOrigin()
                    destinationText = it
                    if (it.length > 2) predictionViewModel.searchPlacesForDestination(it)
                },
                label = { Text("Destination") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        predictionViewModel.clearPredictionsForOrigin()
                    }
            )
            destinationPredictions.forEach { prediction ->
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val placeId = prediction.placeId
                            val placeFields = listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME)
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    val place = response.place
                                    destination = RouteLatLng(
                                        place.location!!.latitude,
                                        place.location!!.longitude
                                    )
                                    destinationText = place.formattedAddress ?: place.displayName
                                            ?: "" // Update the textFiled
                                    predictionViewModel.clearPredictionsForDestination() // Clear the predictions after selecting one
                                    exploreViewModel.fetchRoute(
                                        origin,
                                        destination
                                    ) // Update the Route polyline after select
                                }
                        }
                        .padding(8.dp)
                )
            }
        }

        TravelModeSelector(
            selectedMode = selectedMode,
            onModeSelected = {
                selectedMode = it
                exploreViewModel.fetchRoute(
                    origin,
                    destination,
                    travelMode = selectedMode
                )
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            PlaceTypeSelector(exploreViewModel.placeTypeSelector.collectAsState().value,exploreViewModel::changePlaceType)
            DistanceSlider(exploreViewModel.distanceToPlaces.collectAsState().value,
                exploreViewModel::changeDistanceToPlaces)
            PrimaryButton(
                text = "Check nearby locations",
                backgroundColor = MaterialTheme.colorScheme.primary
            ) { exploreViewModel.getNearbyPlaces() }
        }

        /**
         * Code of route polyline is above
         */

        // GoogleMap Compose
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(60.1699, 24.9384),
                        12f
                    ) // Helsinki in default
                }
            ) {
                if (userLocation.value != null) {
                    Marker(
                        state = rememberUpdatedMarkerState(position = userLocation.value!!),
                        title = "Your location",
                        snippet = "Your current location"
                    )
                }
                if (nearbyLocations.value != null) {
                    for (place in nearbyLocations.value) {
                        Marker(
                            state = rememberUpdatedMarkerState(position = place.location),
                            title = place.displayName.text,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                            tag = place,
                            onClick =
                                {
                                    exploreViewModel.addRouteStop(place)
                                    false
                                }
                        )
                    }
                }


                polyline.value?.let { encoded ->
                    val path = PolyUtil.decode(encoded)
                    Polyline(
                        points = path,
                        color = Color.Blue,
                        width = 8f
                    )
                }

                Marker(
                    state = rememberUpdatedMarkerState(
                        GmsLatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    ),
                    title = "Origin"
                )

                Marker(
                    state = rememberUpdatedMarkerState(
                        GmsLatLng(
                            destination.latitude,
                            destination.longitude
                        )
                    ),
                    title = "Destination"
                )
            }
        }
    }
}

@Composable
fun RouteSummarySection(exploreViewModel: ExploreViewModel) {
    val routeInfo by exploreViewModel.routeInfo.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (routeInfo != null) "$routeInfo" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (com.example.mapapp.data.model.Place) -> Unit,
    selectedRouteStops: List<com.example.mapapp.data.model.Place>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Selected Route Stops",
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for(place:com.example.mapapp.data.model.Place in selectedRouteStops){
                    SelectedStopItem(
                        time = "12:05",
                        locationName = place.displayName.text,
                        distance = "2.7 km",
                        duration = "30 min",
                        placesID = place.id,
                        navigateToLocationScreen = navigateToLocationScreen,
                        onStayTimeChange = { selectedTime ->
                            // handle the selected stay time
                            println("Stay time selected: $selectedTime")
                        },
                        deleteOnClick = { deleteOnClick(place) }
                    )
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                }
            }
        }
    }
}