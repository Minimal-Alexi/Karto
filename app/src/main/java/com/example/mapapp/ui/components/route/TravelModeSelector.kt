package com.example.mapapp.ui.components.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun TravelModeSelector(
    // selectedMode: String,
    // onModeSelected: (String) -> Unit,
) {
    val modes = listOf("Walking", "Cycling", "Driving")
    val icons = listOf(Icons.Default.DirectionsCar,
        Icons.AutoMirrored.Filled.DirectionsWalk, Icons.Default.DirectionsTransit)

    Text("TravelModeSelector")





}