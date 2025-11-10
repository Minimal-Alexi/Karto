package com.example.mapapp.ui.screens

import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.ui.components.DistanceSlider
import com.example.mapapp.ui.components.route.TravelModeSelector
import com.example.mapapp.ui.components.PlaceTypeSelector
import com.example.mapapp.viewmodel.ExploreViewModel
import com.example.mapapp.viewmodel.PredictionViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun ExploreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        MapScreen()
    }
}

@Composable
fun MapScreen(exploreViewModel: ExploreViewModel = viewModel()) {

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

    Column(
        modifier = Modifier.padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
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

        var selectedMode by remember { mutableStateOf("Walking") }

        TravelModeSelector(
            selectedMode = selectedMode,
            onModeSelected = { selectedMode = it }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally){
            PlaceTypeSelector(exploreViewModel.placeTypeSelector.collectAsState().value,exploreViewModel::changePlaceType)
            DistanceSlider(exploreViewModel.distanceToPlaces.collectAsState().value,
                exploreViewModel::changeDistanceToPlaces)
            Button(onClick = { exploreViewModel.getNearbyPlaces() }) {
                Text("Check nearby locations.",style = MaterialTheme.typography.labelLarge)
            }
        }
        /**
         * Code of route polyline is above
         */

        // GoogleMap Compose
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp), // Adjust the top padding as needed
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
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
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