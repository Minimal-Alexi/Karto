package com.example.mapapp.ui.screens.exploreScreenParts

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.SelectedStopItem
import com.example.mapapp.utils.route.RouteViewModel
import kotlin.math.roundToInt

@Composable
fun SelectedStopsSection(
    navigateToLocationScreen: (String) -> Unit,
    deleteOnClick: (Place) -> Unit,
    selectedRouteStops: MutableList<Place>,
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
        ) { Text("sort route for me") }

        Button(
            onClick = {
                routeViewModel.runWithoutSorting()
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
            ReorderableColumn(
                items = selectedRouteStops,
                onMove = { from, to -> selectedRouteStops.move(from, to) }
            ) { item ->
                SelectedStopItem(
                    time = "12:05",
                    locationName = item.displayName.text,
                    distance = if (item.travelDistance == null) "N/A" else item.travelDistance + "m",
                    duration = if (item.travelDuration == null) "N/A" else item.travelDuration + "",
                    placesId = item.id,
                    navigateToLocationScreen = navigateToLocationScreen,
                    onStayTimeChange = { selectedTime ->
                        // handle the selected stay time
                        println("Stay time selected: $selectedTime")
                    },
                    deleteOnClick = { deleteOnClick(item) }
                )
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
    itemContent: @Composable (T) -> Unit
) {
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
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
                    itemContent(item)
                }
            }
        }
    }
}
