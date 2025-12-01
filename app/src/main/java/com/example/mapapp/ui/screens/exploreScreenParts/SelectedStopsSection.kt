package com.example.mapapp.ui.screens.exploreScreenParts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.ui.components.route.StartingLocationCard
import com.example.mapapp.utils.getDistanceLabel
import com.example.mapapp.utils.getTimeLabel
import com.example.mapapp.utils.route.RouteViewModel
import com.example.mapapp.viewmodel.ExploreViewModel

@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (Place) -> Unit,
    selectedRouteStops: MutableList<Place>,
    exploreViewModel: ExploreViewModel,
    routeViewModel: RouteViewModel,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            Column {
                StartingLocationCard(
                    exploreViewModel.customLocationText
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedRouteStops.forEachIndexed { index, place ->
                        SelectedStopItem(
                            index = index,
                            time = "12:05",
                            locationName = place.displayName.text,
                            distance = getDistanceLabel(place.travelDistance),
                            duration = getTimeLabel(place.travelDuration),
                            placesId = place.id,
                            navigateToLocationScreen = navigateToLocationScreen,
                            onStayTimeChange = { selectedTime ->
                                // handle the selected stay time
                                println("Stay time selected: $selectedTime")
                            },
                            deleteOnClick = { deleteOnClick(place) },
                            isFirst = index == 0,
                            isLast = index == selectedRouteStops.lastIndex,
                            onMoveUp = { routeViewModel.moveStopUp(index) },
                            onMoveDown = { routeViewModel.moveStopDown(index) }
                        )
                    }
                }
            }
        }
    }
}