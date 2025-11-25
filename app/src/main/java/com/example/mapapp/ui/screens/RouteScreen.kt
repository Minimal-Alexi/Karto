package com.example.mapapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.navigation.Constants.EXPLORE_SCREEN_ROUTE
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.buttons.SecondaryButton
import com.example.mapapp.viewmodel.RouteScreenViewModel
import androidx.compose.runtime.collectAsState
import com.example.mapapp.data.database.route_stops.RouteStopEntity
import com.example.mapapp.data.database.routes.RouteEntity
import com.example.mapapp.navigation.Constants.SETTINGS_SCREEN_ROUTE
import com.example.mapapp.ui.components.MapWrapper

@Composable
fun RouteScreen(
    navigateToLocationScreen: (String) -> Unit,
    currentRouteViewModel: RouteScreenViewModel = viewModel(),
    navigateToScreen: (String) -> Unit
) {
    val currentRouteFlow = remember { currentRouteViewModel.currentRoute }
    val currentRoute = currentRouteFlow.collectAsState().value

    if (currentRoute != null) {
        CurrentRouteScreen(
            navigateToLocationScreen = navigateToLocationScreen,
            navigateToScreen = navigateToScreen,
            currentRoute = currentRoute
        )
    } else {
        EmptyRouteScreen(navigateToScreen)
    }
}

@Composable
fun EmptyRouteScreen(
    navigateToScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You are not on a Route currently!",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Once you start a Route, it will be here.",
                style = MaterialTheme.typography.titleMedium
            )

            SecondaryButton(
                text = "Make a Route",
                backgroundColor = MaterialTheme.colorScheme.primary,
                onClick = { navigateToScreen(EXPLORE_SCREEN_ROUTE) })
        }
    }
}

@Composable
fun CurrentRouteScreen(
    navigateToLocationScreen: (String) -> Unit,
    navigateToScreen: (String) -> Unit,
    currentRoute: RouteEntity,
) {
    val viewModel = viewModel<RouteScreenViewModel>()
    val mapInteraction = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = !mapInteraction.value
    ) {
        item{
            RouteTitleSection(currentRoute.title)
        }
        item{
            MapWrapper(viewModel,mapInteraction) {RouteScreenMap(viewModel)}
        }
        item{
            OnRouteSection(navigateToLocationScreen)
        }
        item{
            RouteProgressSection()
        }
        item{
            PrimaryButton(
                text = "Complete Route",
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                viewModel.completeRoute()
                navigateToScreen(SETTINGS_SCREEN_ROUTE)
            }
        }
        item{
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

@Composable
fun RouteTitleSection(title : String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun RouteScreenMap(routeScreenViewModel: RouteScreenViewModel) {
    val cameraPositionState = rememberCameraPositionState()
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }

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
    navigateToLocationScreen: (String) -> Unit,
) {
    val viewModel = viewModel<RouteScreenViewModel>()

    val currentRouteStopsFlow = remember { viewModel.currentStops }
    val currentStops = currentRouteStopsFlow.collectAsState().value

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
                if (currentStops != null) {
                    for (location in currentStops){
                        RouteStopItem(
                            time = "12:05",
                            location = location,
                            distance = "2.7 km",
                            duration = "30 min",
                            navigateToLocationScreen = navigateToLocationScreen,
                            markLocationVisit = viewModel::markRouteStopVisit
                        )
                        HorizontalDivider(color = Color(0xFFDDDDDD))
                    }
                } else {
                    Text("No stops on this route!")
                }
            }
        }
    }
}

@Composable
fun RouteStopItem(
    time: String,
    location: RouteStopEntity,
    distance: String,
    duration: String,
    closingInfo: String? = null,
    navigateToLocationScreen: (String) -> Unit,
    markLocationVisit: (RouteStopEntity) -> Unit
) {

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
                            color = if (location.isVisited) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            shape = CircleShape
                        )
                        .background(
                            color = if (location.isVisited) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            markLocationVisit(location)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (location.isVisited) {
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
                        text = location.name,
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
                    navigateToLocationScreen(location.placesId)
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