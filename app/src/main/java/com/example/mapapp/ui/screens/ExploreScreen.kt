package com.example.mapapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.Place
import com.example.mapapp.navigation.Constants.ROUTE_SCREEN_ROUTE
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.MapPlaceInfoCard
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.ui.components.MapRouteStopInfoCard
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.route.TravelModeSelector
import com.example.mapapp.viewmodel.ExploreViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun ExploreScreen(navigateToLocationScreen: (String) -> Unit,
                  exploreViewModel: ExploreViewModel = viewModel(),
                  navigateToScreen : (String) -> Unit,
                  openedRouteId: Int? = null) {

    LaunchedEffect(openedRouteId) {
        if (openedRouteId != null) {
            exploreViewModel.loadSavedRoute(openedRouteId)
        }
    }

    val mapInteraction = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = !mapInteraction.value
    ) {
        item {
            OutlinedTextField(
                value = exploreViewModel.routeTitle.value,
                onValueChange = { exploreViewModel.routeTitle.value = it },
                label = { Text("Route Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        item {
            NearbyPlaceSelector(exploreViewModel)
        }
        item {
            TravelModeSelector(
                exploreViewModel.travelMode.collectAsState().value,
                exploreViewModel::changeTravelMode
            )
        }
        item { MapWrapper(exploreViewModel, mapInteraction) }

        item {
            SelectedStopsSection(
                navigateToLocationScreen,
                exploreViewModel::removeRouteStop,
                exploreViewModel.routeStops.collectAsState().value,
                exploreViewModel = exploreViewModel
            )
        }
        item { RouteSummarySection(exploreViewModel) }
        item {
            PrimaryButton(
                text = "Start This Route",
                backgroundColor = MaterialTheme.colorScheme.secondary
            ) {
                exploreViewModel.startRoute()
                navigateToScreen(ROUTE_SCREEN_ROUTE)
            }
        }
        item {
            PrimaryButton(
                text = "Save This Route For Later",
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                exploreViewModel.saveRoute()
            }
        }
        item {
            PrimaryButton(
                text = "Reset This Route", backgroundColor = MaterialTheme.colorScheme.error
            ) { /* TODO */ }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun MapWrapper(exploreViewModel: ExploreViewModel, mapInteraction: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // fixed height is important
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // wait for the first down
                        val down = awaitFirstDown(requireUnconsumed = false)
                        mapInteraction.value = true
                        // keep reading pointer events until all pointers are up
                        do {
                            val event = awaitPointerEvent()
                            // optional: you can examine event.changes to consume if needed
                        } while (event.changes.any { it.pressed })

                        mapInteraction.value = false
                    }
                }
            }) {
        MapScreen(exploreViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(exploreViewModel: ExploreViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedPlaceIsRouteStop by remember {mutableStateOf<Boolean>(false)}
    val userLocation = exploreViewModel.userLocation.collectAsState()
    val routeStops = exploreViewModel.routeStops.collectAsState()
    val nearbyLocations = exploreViewModel.nearbyPlaces.collectAsState()
    val polyline = exploreViewModel.routePolyline.collectAsState()

    // GoogleMap Compose
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(), cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(60.1699, 24.9384), 12f
                ) // Helsinki in default
            }) {
            if (userLocation.value != null) {
                Marker(
                    state = rememberUpdatedMarkerState(position = userLocation.value!!),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    title = "Your location",
                    snippet = "Your current location"
                )
            }
            if (nearbyLocations.value != null) {
                for (place in nearbyLocations.value) {
                    if (!routeStops.value.contains(place)) {
                        Marker(
                            state = rememberUpdatedMarkerState(position = place.location),
                            title = place.displayName.text,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                place.typeOfPlace?.markerColor ?: BitmapDescriptorFactory.HUE_RED
                            ),
                            tag = place,
                            onClick = {
                                selectedPlace = if(place == selectedPlace) null
                                else {
                                    selectedPlaceIsRouteStop = false
                                    place
                                }
                                true
                            }
                            )
                    }
                }
            }
            for (place in routeStops.value) {
                Marker(
                    state = rememberUpdatedMarkerState(position = place.location),
                    title = place.displayName.text,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    tag = place,
                    onClick = {
                        selectedPlace = if(place == selectedPlace) null
                        else {
                            selectedPlaceIsRouteStop = true
                            place
                        }
                        true
                    }
                    )
            }

            if (polyline.value != null) {
                val points = PolyUtil.decode(polyline.value)
                Polyline(
                    points = points,
                    color = MaterialTheme.colorScheme.primary,
                    width = 10f
                )
            }
        }
    }
    if(selectedPlace != null){
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null },
            sheetState = sheetState
        ) {
            Column(Modifier.padding(16.dp)) {
                if(selectedPlaceIsRouteStop) MapRouteStopInfoCard(selectedPlace!!,
                    {
                        exploreViewModel.removeRouteStop(selectedPlace!!)
                        selectedPlace = null
                    })
                else MapPlaceInfoCard(selectedPlace!!,
                    {
                        exploreViewModel.addRouteStop(selectedPlace!!)
                        selectedPlace = null
                    })
            }
        }
    }
}

@Composable
fun RouteSummarySection(exploreViewModel: ExploreViewModel) {
    val routeInfo by exploreViewModel.routeInfo.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Summary", style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)
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
fun NearbyPlaceSelector(exploreViewModel: ExploreViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PlaceTypeSelector(
            exploreViewModel.placeTypeSelector.collectAsState().value,
            exploreViewModel::changePlaceType
        )
        DistanceSlider(
            exploreViewModel.distanceToPlaces.collectAsState().value,
            exploreViewModel::changeDistanceToPlaces
        )
        PrimaryButton(
            text = "Check nearby locations", backgroundColor = MaterialTheme.colorScheme.primary
        ) { exploreViewModel.getNearbyPlaces() }
    }
}

@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (com.example.mapapp.data.model.Place) -> Unit,
    selectedRouteStops: List<com.example.mapapp.data.model.Place>,
    exploreViewModel: ExploreViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Selected Route Stops", style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = {
                exploreViewModel.runMatrixFlow()
            }
        ) { Text("Calculate route") }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (place: com.example.mapapp.data.model.Place in selectedRouteStops) {
                    SelectedStopItem(
                        time = "12:05",
                        locationName = place.displayName.text,
                        distance = "2.7 km",
                        duration = "30 min",
                        placesId = place.id,
                        navigateToLocationScreen = navigateToLocationScreen,
                        onStayTimeChange = { selectedTime ->
                            // handle the selected stay time
                            println("Stay time selected: $selectedTime")
                        },
                        deleteOnClick = { deleteOnClick(place) })
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                }
            }
        }
    }
}