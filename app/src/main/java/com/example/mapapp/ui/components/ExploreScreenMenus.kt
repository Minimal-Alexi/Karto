package com.example.mapapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.navigation.Constants.ROUTE_SCREEN_ROUTE
import com.example.mapapp.ui.components.buttons.ExploreMenuButton
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.route.TravelModeSelector
import com.example.mapapp.ui.screens.EditableHeading
import com.example.mapapp.ui.screens.NearbyPlaceSelector
import com.example.mapapp.ui.screens.RouteSummarySection
import com.example.mapapp.ui.screens.exploreScreenParts.SelectedStopsSection
import com.example.mapapp.utils.route.RouteViewModel
import com.example.mapapp.viewmodel.ExploreViewModel

@Composable
fun TopMenu(expanded: MutableState<Boolean>) {
    val exploreViewModel: ExploreViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                EditableHeading(exploreViewModel.routeTitle)

                if (expanded.value) {
                    NearbyPlaceSelector(expanded)
                }
            }

            ExploreMenuButton(
                expanded,
                Icons.Default.KeyboardArrowUp,
                Icons.Default.KeyboardArrowDown
            )
        }
    }
}

@Composable
fun BottomMenu(
    expanded: MutableState<Boolean>,
    navigateToLocationScreen: (String) -> Unit,
    exploreViewModel: ExploreViewModel = viewModel(),
    routeViewModel: RouteViewModel = viewModel(),
    navigateToScreen: (String) -> Unit,
    openedRouteId: Int? = null,
    onResetRoute: () -> Unit
) {
    val routeStopAmount = exploreViewModel.routeStops.collectAsState().value.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp)
            .heightIn(max = 440.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column {
            ExploreMenuButton(
                expanded,
                Icons.Default.KeyboardArrowDown,
                Icons.Default.KeyboardArrowUp
            )

            Text(
                text = "Selected Route Stops${if (routeStopAmount > 0) " (${routeStopAmount})" else ""}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(12.dp, 4.dp)
            )


            if (expanded.value) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (routeStopAmount > 0) {
                        TravelModeSelector(
                            exploreViewModel.travelMode.collectAsState().value,
                            exploreViewModel::changeTravelMode
                        )

                        SelectedStopsSection(
                            navigateToLocationScreen,
                            exploreViewModel::removeRouteStop,
                            exploreViewModel.routeStops.collectAsState().value,
                            exploreViewModel = exploreViewModel,
                            routeViewModel = routeViewModel
                        )

                        RouteSummarySection(exploreViewModel)
                        PrimaryButton(
                            text = "Start This Route",
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            enabled = (exploreViewModel.routeStops.collectAsState().value.isNotEmpty() && exploreViewModel.userLocation.collectAsState().value != null)
                        ) {
                            exploreViewModel.startRoute()
                            navigateToScreen(ROUTE_SCREEN_ROUTE)
                        }
                        PrimaryButton(
                            text = if (openedRouteId != null) "Update This Saved Route" else "Save This Route For Later",
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            enabled = (exploreViewModel.routeStops.collectAsState().value.isNotEmpty() && exploreViewModel.userLocation.collectAsState().value != null)
                        ) {
                            if (openedRouteId != null) exploreViewModel.updateSavedRoute(
                                openedRouteId
                            )
                            else
                                exploreViewModel.saveRoute()
                        }

                        PrimaryButton(
                            text = "Reset This Route",
                            backgroundColor = MaterialTheme.colorScheme.error
                        ) {
                            exploreViewModel.resetRoute()
                            onResetRoute()
                        }

                    } else {
                        Text(
                            text = "No selected route stops - Add stops from the map!",
                            modifier = Modifier
                                .padding(12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}