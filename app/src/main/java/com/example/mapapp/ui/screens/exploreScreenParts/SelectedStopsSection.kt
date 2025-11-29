package com.example.mapapp.ui.screens.exploreScreenParts

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.utils.route.RouteViewModel
import com.example.mapapp.viewmodel.ExploreViewModel
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import com.example.mapapp.ui.components.buttons.PrimaryButton
import com.example.mapapp.ui.components.route.StartingLocationCard
import com.example.mapapp.utils.getTimeLabel
import com.example.mapapp.utils.getDistanceLabel

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
        var checkedAutoSort by remember { mutableStateOf(true) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checkedAutoSort,
                onCheckedChange = { checkedAutoSort = it })
            Text("Reorder route to be optimal")
        }

        PrimaryButton(
            text = "Calculate Route",
            backgroundColor = MaterialTheme.colorScheme.primary,
        ) {
            if (checkedAutoSort) {
                routeViewModel.runMatrixFlow()
            } else {
                routeViewModel.runWithoutSorting()
            }
        }

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
                /*
                ReorderableColumn(
                    items = selectedRouteStops,
                    onMove = { from, to -> selectedRouteStops.move(from, to) },
                ) { item, index ->
                    SelectedStopItem(
                        time = "12:05",
                        locationName = item.displayName.text,
                        distance = if (item.travelDistance == null) "N/A" else item.travelDistance + "m",
                        duration = if (item.travelDuration == null) "N/A" else item.travelDuration + "",
                        placesId = item.id,
                        index = index,
                        navigateToLocationScreen = navigateToLocationScreen,
                        onStayTimeChange = { selectedTime ->
                            // handle the selected stay time
                            println("Stay time selected: $selectedTime")
                        },
                        deleteOnClick = { deleteOnClick(item) }
                    )
                }
                 */
            }
        }
    }
}

fun <T> MutableList<T>.move(from: Int, to: Int) {
    val tmp = removeAt(from)
    add(to, tmp)
}

@Composable
fun <T> ReorderableColumn(
    items: List<T>,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T, Int) -> Unit,
) {
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier) {
        Column {
            items.forEachIndexed { index, item ->
                val isDragging = draggingIndex == index

                Box(
                    Modifier
                        .onGloballyPositioned {
                            itemHeights[index] = it.size.height
                        }
                        .zIndex(if (isDragging) 1f else 0f)
                        .offset {
                            val y = if (isDragging) dragOffset.roundToInt() else 0
                            IntOffset(0, y)
                        }
                        .pointerInput(index) {
                            detectDragGestures(
                                onDragStart = { draggingIndex = index },
                                onDragEnd = {
                                    draggingIndex = null
                                    dragOffset = 0f
                                },
                                onDragCancel = {
                                    draggingIndex = null
                                    dragOffset = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()

                                    val height = itemHeights[index] ?: 0
                                    dragOffset += dragAmount.y

                                    val targetIndex = when {
                                        dragOffset > height / 2 && index < items.lastIndex ->
                                            index + 1

                                        dragOffset < -height / 2 && index > 0 ->
                                            index - 1

                                        else -> null
                                    }

                                    if (targetIndex != null) {
                                        onMove(index, targetIndex)
                                        draggingIndex = targetIndex
                                        dragOffset = 0f
                                    }
                                }
                            )
                        }
                        .graphicsLayer {
                            if (isDragging) {
                                scaleX = 1.02f
                                scaleY = 1.02f
                                shadowElevation = 16f
                            }
                        }
                ) {
                    itemContent(item, index)
                }
            }
        }
    }
}
