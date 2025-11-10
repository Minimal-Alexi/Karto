package com.example.mapapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.data.model.RouteLatLng
import com.example.mapapp.viewmodel.ExploreViewModel
import com.example.mapapp.viewmodel.PredictionViewModel
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
fun MapScreen(exploreViewModel: ExploreViewModel = viewModel()) {

    val userLocation = exploreViewModel.userLocation.collectAsState()
    val nearbyLocations = exploreViewModel.nearbyPlaces.collectAsState()
    val helsinki = LatLng(60.1699, 24.9384)

    val polyline = exploreViewModel.routePolyline.collectAsState()

    var origin = RouteLatLng(60.1699, 24.9384)
    var destination = RouteLatLng(60.2055, 24.6559)

    LaunchedEffect(Unit) {
        exploreViewModel.fetchRoute(origin, destination)
    }

    /**

     */
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val predictionViewModel = remember { PredictionViewModel(placesClient) }

    var query by remember { mutableStateOf("") }
    val predictions by predictionViewModel.originPredictions.collectAsState()

    var originLatLng by remember { mutableStateOf<RouteLatLng?>(null) }
    var destinationLatLng by remember {
        mutableStateOf<RouteLatLng?>(
            null
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.length > 2) predictionViewModel.searchPlacesForOrigin(it)
            },
            label = { Text("Search Places") },
            modifier = Modifier.fillMaxWidth()
        )

        predictions.forEach { prediction ->
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
                                destinationLatLng = RouteLatLng(place.location!!.latitude, place.location!!.longitude)
                                destination = RouteLatLng(place.location!!.latitude, place.location!!.longitude)
                            }

                    }
                    .padding(8.dp)
            )
        }

        destinationLatLng?.let { latLng ->
            Text(
                text = ", latitude: ${latLng.latitude}, longitude: ${latLng.longitude}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }


    /**
     *
     */


    Box(modifier = Modifier.fillMaxSize()) {
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
            if(nearbyLocations.value != null){
                for(place in nearbyLocations.value){
                    Marker(
                        state = rememberUpdatedMarkerState(position = place.location),
                        title = place.displayName.text,
                    )
                }
            }
            Marker(
                state = rememberUpdatedMarkerState(position = helsinki),
                title = "Helsinki",
                snippet = "Marker in Helsinki"
            )



            destinationLatLng?.let {
                polyline.value?.let { encoded ->
                    val path = PolyUtil.decode(encoded)
                    Polyline(
                        points = path,
                        color = Color.Blue,
                        width = 8f
                    )
                }

                Marker(
                    state = rememberUpdatedMarkerState(GmsLatLng(origin.latitude, origin.longitude)),
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
        Button(onClick = { exploreViewModel.getNearbyPlaces() }) {
            Text("Check nearby locations.")
        }
    }
}


@Composable
fun RouteInput(){
    var originText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }


    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val predictionViewModel = remember { PredictionViewModel(placesClient) }

    var query by remember { mutableStateOf("") }
    val predictions by predictionViewModel.originPredictions.collectAsState()

    var originLatLng by remember { mutableStateOf<RouteLatLng?>(null) }
    var destinationLatLng by remember {
        mutableStateOf<RouteLatLng?>(
            null
        )
    }


    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.length > 2) predictionViewModel.searchPlacesForOrigin(it)
            },
            label = { Text("Search Places") },
            modifier = Modifier.fillMaxWidth()
        )

        predictions.forEach { prediction ->
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
                                destinationLatLng = RouteLatLng(place.location!!.latitude, place.location!!.longitude)
                            }

                    }
                    .padding(8.dp)
            )
        }

        destinationLatLng?.let { latLng ->
            Text(
                text = ", latitude: ${latLng.latitude}, longitude: ${latLng.longitude}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}