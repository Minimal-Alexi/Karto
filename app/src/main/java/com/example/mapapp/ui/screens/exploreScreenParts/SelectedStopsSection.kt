package com.example.mapapp.ui.screens.exploreScreenParts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.ui.components.route.StartingLocationCard
import com.example.mapapp.utils.getDistanceLabel
import com.example.mapapp.utils.getTimeLabel
import com.example.mapapp.utils.route.ExploreViewModelRouteUtil
import com.example.mapapp.viewmodel.ExploreViewModel
import com.example.mapapp.ui.components.Placeholder
import com.example.mapapp.utils.DialogData


@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (Place) -> Unit,
    selectedRouteStops: MutableList<Place>,
    exploreViewModel: ExploreViewModel,
    exploreViewModelRouteUtil: ExploreViewModelRouteUtil,
) {
    val dialogDataState = exploreViewModel.dialogDataState

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val errorColor = MaterialTheme.colorScheme.error

                    if (selectedRouteStops.isNotEmpty()) {
                        selectedRouteStops.forEachIndexed { index, place ->
                            SelectedStopItem(
                                index = index,
                                locationName = place.displayName.text,
                                distance = getDistanceLabel(place.travelDistance),
                                duration = getTimeLabel(place.travelDuration),
                                placesId = place.id,
                                navigateToLocationScreen = navigateToLocationScreen,
                                onStayTimeChange = { selectedTime ->
                                    // handle the selected stay time
                                    println("Stay time selected: $selectedTime")
                                },
                                deleteOnClick = {
                                    dialogDataState.value = DialogData(
                                        title = "Delete ${place.displayName.text} from your Route?",
                                        confirmLabel = "Delete",
                                        onConfirm = { deleteOnClick(place) },
                                        confirmColor = errorColor,
                                        dismissLabel = "Cancel",
                                        isShowing = mutableStateOf(true)
                                    )
                                },
                                isFirst = index == 0,
                                isLast = index == selectedRouteStops.lastIndex,
                                onMoveUp = { exploreViewModelRouteUtil.moveStopUp(index) },
                                onMoveDown = { exploreViewModelRouteUtil.moveStopDown(index) }
                            )
                        }
                    } else {
                        Placeholder(text = "Your route does not have stops yet. Add stops from the map to build your route.")
                    }
                }
            }
        }
    }
}