package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
        Text(text = "Hello Explore Screen!", fontSize = 20.sp)
        MapScreen()
    }
}

@Composable
fun MapScreen(mapViewModel: MapViewModel = viewModel()) {

    val userLocation = mapViewModel.userLocation.collectAsState()
    val helsinki = LatLng(60.1699, 24.9384)
    val espoo = LatLng(60.2055, 24.6559)

    val polylinePoints = listOf(helsinki, espoo)


    Box(modifier = Modifier.fillMaxSize()) {
        // GoogleMap Compose
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp), // Adjust the top padding as needed
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(60.1699, 24.9384), 12f) // Helsinki in default
            }
        ) {
            if(userLocation.value != null){
                Marker(
                    state = rememberUpdatedMarkerState(position = userLocation.value!!),
                            title = "Your location",
                    snippet = "Your current location"
                )
            }
            Marker(
                state = rememberUpdatedMarkerState(position = helsinki),
                title = "Helsinki",
                snippet = "Marker in Helsinki"
            )

            Polyline(
                points = polylinePoints,
                color = Color.Blue,
                width = 10f
            )
        }
    }
}