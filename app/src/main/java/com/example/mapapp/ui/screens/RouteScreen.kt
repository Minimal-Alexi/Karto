package com.example.mapapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.mapapp.ui.components.PrimaryButton

@Composable
fun RouteScreen(navigateToLocationScreen: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RouteTitleSection()
        MapScreenPlaceholder()
        OnRouteSection(navigateToLocationScreen)
        RouteProgressSection()
        PrimaryButton(
            text = "Pause This Route",
            backgroundColor = MaterialTheme.colorScheme.error
        ) {
            /* TODO: Pause route */
        }
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
fun RouteTitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Beaches of Helsinki",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = { /* TODO: Edit route title */ }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit route",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun MapScreenPlaceholder() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Request current location once
    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(currentLatLng!!, 14f)
                }
            }
        } catch (e: SecurityException) {
            // Make sure you have location permission granted before calling this
            e.printStackTrace()
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = cameraPositionState
    ) {
        currentLatLng?.let { position ->
            Marker(
                state = MarkerState(position = position),
                title = "You are here"
            )
        }
    }
}

@Composable
fun OnRouteSection(
    navigateToLocationScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "On Route",
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
                RouteStopItem(
                    time = "12:05",
                    locationName = "Mustikkamaan ranta",
                    distance = "2.7 km",
                    duration = "30 min",
                    placesID = "ChIJqwoUM14JkkYRtUIe2tzlrF8",
                    navigateToLocationScreen = navigateToLocationScreen
                )
                HorizontalDivider(color = Color(0xFFDDDDDD))
                RouteStopItem(
                    time = "12:55",
                    locationName = "Karhusaari Beach",
                    distance = "2.7 km",
                    duration = "30 min",
                    closingInfo = "Closes at 16:00",
                    placesID = "ChIJabi06Yb1jUYRh5VZ9yyiOr8",
                    navigateToLocationScreen = navigateToLocationScreen
                )
                HorizontalDivider(color = Color(0xFFDDDDDD))
                RouteStopItem(
                    time = "13:40",
                    locationName = "Vetokannas Swimming Beach",
                    distance = "2.7 km",
                    duration = "30 min",
                    placesID = "ChIJ2YucHr33jUYRoa1UdtWwqSM",
                    navigateToLocationScreen = navigateToLocationScreen
                )
                HorizontalDivider(color = Color(0xFFDDDDDD))
                RouteStopItem(
                    time = "14:25",
                    locationName = "Hietaranta Beach",
                    distance = "2.7 km",
                    duration = "30 min",
                    placesID = "ChIJC4WpRTkKkkYRH_pPtYjChjg",
                    navigateToLocationScreen = navigateToLocationScreen
                )
            }
        }
    }
}

@Composable
fun RouteStopItem(
    time: String,
    locationName: String,
    distance: String,
    duration: String,
    closingInfo: String? = null,
    placesID: String,
    navigateToLocationScreen: (String) -> Unit
) {
    var isVisited by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            width = 2.dp,
                            color = if (isVisited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            shape = CircleShape
                        )
                        .background(
                            color = if (isVisited) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isVisited = !isVisited
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isVisited) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Visited",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(240.dp)
                    )

                    closingInfo?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Book icon on the right
            IconButton(
                onClick = {
                    navigateToLocationScreen(placesID)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = "Read more",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun RouteProgressSection() {
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
                    text = "1 / 4 locations visited",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "1.2 / 5.4 km walked",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "10 minutes 24 seconds spent on route",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}