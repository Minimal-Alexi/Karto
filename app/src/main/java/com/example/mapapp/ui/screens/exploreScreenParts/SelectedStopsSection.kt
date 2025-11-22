package com.example.mapapp.ui.screens.exploreScreenParts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.utils.route.RouteViewModel

@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (com.example.mapapp.data.model.Place) -> Unit,
    selectedRouteStops: List<com.example.mapapp.data.model.Place>,
    selectedRouteStopsInfo: List<com.example.mapapp.data.model.Leg>,
    routeViewModel: RouteViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Selected Route Stops", style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = {
                routeViewModel.runMatrixFlow()
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
                for (i in selectedRouteStops.indices) {
                    SelectedStopItem(
                        time = "12:05",
                        locationName = selectedRouteStops[i].displayName.text,
                        distance = if (selectedRouteStopsInfo.isEmpty()) "N/A" else selectedRouteStopsInfo[i].distanceMeters.toString() + "m",
                        duration = if (selectedRouteStopsInfo.isEmpty()) "N/A" else selectedRouteStopsInfo[i].duration.toString(),
                        placesId = selectedRouteStops[i].id,
                        navigateToLocationScreen = navigateToLocationScreen,
                        onStayTimeChange = { selectedTime ->
                            // handle the selected stay time
                            println("Stay time selected: $selectedTime")
                        },
                        deleteOnClick = { deleteOnClick(selectedRouteStops[i]) })
                    HorizontalDivider(color = Color(0xFFDDDDDD))
                }
            }
        }
    }
}