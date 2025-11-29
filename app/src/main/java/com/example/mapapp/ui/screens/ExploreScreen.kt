package com.example.mapapp.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.android.gms.maps.model.StrokeStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.map.MapPlaceInfoCard
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.ui.components.map.MapRouteStopInfoCard
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.route.StartingLocationSelector
import com.example.mapapp.utils.route.RouteViewModel
import com.example.mapapp.viewmodel.ExploreViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.StyleSpan
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import java.lang.reflect.Array.set

@Composable
fun ExploreScreen(
    navigateToLocationScreen: (String) -> Unit,
    exploreViewModel: ExploreViewModel = viewModel(),
    routeViewModel: RouteViewModel = viewModel(),
    navigateToScreen: (String) -> Unit,
    openedRouteId: Int? = null,
    onResetRoute: () -> Unit
) {
    LaunchedEffect(openedRouteId) {
        if (openedRouteId != null) {
            exploreViewModel.loadSavedRoute(openedRouteId)
        }
    }

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

    Column(modifier = Modifier.fillMaxSize()) {
        TopMenu(topShowing)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ExploreScreenMap(exploreViewModel, topShowing, bottomShowing)
        }

        BottomMenu(bottomShowing)
    }

/*    Box {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
        ) {
            TopMenu(topShowing)
        }

        ExploreScreenMap(exploreViewModel, topShowing, bottomShowing)

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            BottomMenu(bottomShowing)
        }
    }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreenMap(
    exploreViewModel: ExploreViewModel,
    topExpanded: MutableState<Boolean>,
    bottomExpanded: MutableState<Boolean>
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
    val polyline = exploreViewModel.routePolyline.collectAsState()/*
    Camera position value handling
    */

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(60.1699, 24.9384), 12f
        )
    }

    LaunchedEffect(userLocation.value, nearbyLocations.value, routeStops.value) {
        val user = userLocation.value
        val nearby = nearbyLocations.value ?: emptyList()
        val stops = routeStops.value

        if (user != null) {
            val builder = LatLngBounds.builder()

            builder.include(user)

            nearby.forEach { builder.include(it.location) }
            stops.forEach { builder.include(it.location) }

            try {
                if (nearby.isNotEmpty() || stops.isNotEmpty()) {
                    val bounds = builder.build()
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 150)
                    )
                } else {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(user, 15f)
                    )
                }
            } catch (e: Exception) {
                // invalid bounds cases
            }
        }
    }

    // GoogleMap Compose
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState
        ) {
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

            if (polyline.value != null) {
                val points = PolyUtil.decode(polyline.value)
                Polyline(
                    points = points,
                    width = 10f,
                    geodesic = true, // Follows the curvature of the earth for better directionality
                    // Add a CustomCap to create an arrow at the end of the line
                    endCap = CustomCap(
                        BitmapDescriptorFactory.fromResource(R.drawable.arrow_up_float)
                        // Note: Replace 'android.R.drawable.arrow_up_float' with your own
                        // drawable (e.g., R.drawable.ic_arrow_head) for the best look.
                    ),
                    spans = listOf(
                        StyleSpan(
                            StrokeStyle.gradientBuilder(
                                MaterialTheme.colorScheme.primary.hashCode(),
                                MaterialTheme.colorScheme.secondary.hashCode()
                            ).build(), 1.0 // Apply to 100% of the line
                        )
                    ),

                    )
            }
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
                        bottomExpanded.value = true
                    })
            }
        }
    }
}

@Composable
fun TopMenu(expanded: MutableState<Boolean>) {
    val exploreViewModel: ExploreViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(6.dp, 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            EditableHeading(exploreViewModel.routeTitle)

            if (expanded.value) {
                NearbyPlaceSelector(expanded)
            }

            Button(
                onClick = { expanded.value = !expanded.value }, modifier = Modifier.fillMaxWidth()
            ) {
                if (expanded.value) {
                    Text("close")
                } else {
                    Text("open")
                }
            }
        }
    }
}

@Composable
fun BottomMenu(expanded: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(6.dp, 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {
        LazyColumn {
            item {
                Button(
                    onClick = { expanded.value = !expanded.value },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (expanded.value) {
                        Text("close")
                    } else {
                        Text("open")
                    }
                }
            }

            if (expanded.value) {
                item {
                    Text("thanks for expanding me")
                }
            }
        }
    }
}

@Composable
fun EditableHeading(routeTitle: MutableState<String>) {
    OutlinedTextField(
        value = routeTitle.value,
        onValueChange = { routeTitle.value = it },
        label = { Text("Route Title") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun NearbyPlaceSelector(expanded: MutableState<Boolean>) {
    val exploreViewModel: ExploreViewModel = viewModel()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

