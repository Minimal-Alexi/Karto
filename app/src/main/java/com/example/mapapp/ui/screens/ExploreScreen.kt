package com.example.mapapp.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import com.google.maps.android.SphericalUtil
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.BottomMenu
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.ui.components.TopMenu
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.map.MapPlaceInfoCard
import com.example.mapapp.ui.components.map.MapPolyline
import com.example.mapapp.ui.components.map.MapRouteStopInfoCard
import com.example.mapapp.ui.components.route.StartingLocationSelector
import com.example.mapapp.utils.getDistanceLabel
import com.example.mapapp.utils.getTimeLabel
import com.example.mapapp.utils.route.ExploreViewModelRouteUtil
import com.example.mapapp.viewmodel.ExploreViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun ExploreScreen(
    navigateToLocationScreen: (String) -> Unit,
    exploreViewModel: ExploreViewModel = viewModel(),
    exploreViewModelRouteUtil: ExploreViewModelRouteUtil = viewModel(),
    navigateToScreen: (String) -> Unit,
    openedRouteId: Int? = null,
    onResetRoute: () -> Unit,
) {
    val _top = remember { mutableStateOf(true) }
    val _bottom = remember { mutableStateOf(false) }

    val topShowing = remember {
        object : MutableState<Boolean> by _top {
            override var value: Boolean
                get() = _top.value
                set(newValue) {
                    if (newValue) _bottom.value = false
                    _top.value = newValue
                }
        }
    }

    val bottomShowing = remember {
        object : MutableState<Boolean> by _bottom {
            override var value: Boolean
                get() = _bottom.value
                set(newValue) {
                    if (newValue) _top.value = false
                    _bottom.value = newValue
                }
        }
    }

    LaunchedEffect(openedRouteId) {
        if (openedRouteId != null) {
            exploreViewModel.loadTemplate(openedRouteId)
            bottomShowing.value = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.zIndex(1f)) {
            TopMenu(topShowing)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .zIndex(0f)
        ) {
            ExploreScreenMap(exploreViewModel)
        }

        Box(modifier = Modifier.zIndex(1f)) {
            BottomMenu(
                expanded = bottomShowing,
                navigateToLocationScreen = navigateToLocationScreen,
                exploreViewModel = exploreViewModel,
                exploreViewModelRouteUtil = exploreViewModelRouteUtil,
                navigateToScreen = navigateToScreen,
                openedRouteId = openedRouteId,
                onResetRoute = onResetRoute
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreenMap(
    exploreViewModel: ExploreViewModel,
) {/*
    Selected place info card handler
    */
    val sheetState = rememberModalBottomSheetState()
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedPlaceIsRouteStop by remember { mutableStateOf<Boolean>(false) }/*
    Map Logic Values
    */
    val userLocation = exploreViewModel.userLocation.collectAsState()
    val routeStops = exploreViewModel.routeStops.collectAsState()
    val nearbyLocations = exploreViewModel.nearbyPlaces.collectAsState()

    /*
    Camera position value handling
    */

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(60.1699, 24.9384), 12f
        )
    }
    var mapReady by remember { mutableStateOf(false) }
    LaunchedEffect(userLocation.value, mapReady) {
        val loc = userLocation.value
        if (loc != null && mapReady) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 15f)
            )
        }
    }

    // GoogleMap Compose
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { mapReady = true }) {
            if (userLocation.value != null) {
                Marker(
                    state = rememberUpdatedMarkerState(position = userLocation.value!!),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    title = "Route Origin",
                    snippet = "Your starting location"
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
                                selectedPlace = if (place == selectedPlace) null
                                else {
                                    selectedPlaceIsRouteStop = false
                                    place
                                }
                                true
                            })
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
                        selectedPlace = if (place == selectedPlace) null
                        else {
                            selectedPlaceIsRouteStop = true
                            place
                        }
                        true
                    })
            }

            MapPolyline(exploreViewModel, cameraPositionState.position.zoom)

        }
    }
    if (selectedPlace != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null }, sheetState = sheetState
        ) {
            Column(Modifier.padding(16.dp)) {
                if (selectedPlaceIsRouteStop) MapRouteStopInfoCard(
                    selectedPlace!!, {
                        exploreViewModel.removeRouteStop(selectedPlace!!)
                        selectedPlace = null
                    })
                else MapPlaceInfoCard(
                    selectedPlace!!, {
                        exploreViewModel.addRouteStop(selectedPlace!!)
                        selectedPlace = null
                    })
            }
        }
    }
}

@Composable
fun EditableHeading(routeTitle: MutableState<String>) {
    val beingEdited = remember { mutableStateOf(false) }
    val textFieldValue = remember { mutableStateOf(routeTitle.value) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp)
    ) {
        if (beingEdited.value) {
            OutlinedTextField(
                value = textFieldValue.value,
                onValueChange = { textFieldValue.value = it },
                label = { Text("Route Title") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        routeTitle.value = textFieldValue.value
                        beingEdited.value = false
                    }
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    routeTitle.value = textFieldValue.value
                    beingEdited.value = false
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = "Save Route Title",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Text(
                text = routeTitle.value,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { beingEdited.value = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit Route Title",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NearbyPlaceSelector(expanded: MutableState<Boolean>) {
    val exploreViewModel: ExploreViewModel = viewModel()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        PlaceTypeSelector(
            exploreViewModel.placeTypeSelector.collectAsState().value,
            exploreViewModel::changePlaceType
        )

        StartingLocationSelector()

        DistanceSlider(
            exploreViewModel.distanceToPlaces.collectAsState().value,
            exploreViewModel::changeDistanceToPlaces
        )

        PrimaryButton(
            text = "Check nearby locations", backgroundColor = MaterialTheme.colorScheme.primary
        ) {
            exploreViewModel.getNearbyPlaces()
            expanded.value = false
        }
    }
}

@Composable
fun RouteSummarySection(exploreViewModel: ExploreViewModel) {
    val routeTime by exploreViewModel.routeTime.collectAsState()
    val routeDistance by exploreViewModel.routeDistance.collectAsState()

    /** don't render an empty summary */
    if (routeTime == null || routeDistance == null) {
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Summary", style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(0.dp, 16.dp)
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
                    text = if (routeTime != null) "Total Travel Time: ${getTimeLabel(routeTime)}" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (routeDistance != null) "Total Travel Distance: ${
                        getDistanceLabel(
                            routeDistance
                        )
                    }" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

