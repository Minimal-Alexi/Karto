package com.example.mapapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.theme.MapAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Composable
fun MapScreen(
) {

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