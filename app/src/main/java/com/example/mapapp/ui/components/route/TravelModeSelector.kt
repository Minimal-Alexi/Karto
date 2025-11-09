package com.example.mapapp.ui.components.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TravelModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
) {
    val modes = listOf("Walking", "Cycling", "Driving")
    val icons = listOf(
        Icons.Default.DirectionsCar,
        Icons.AutoMirrored.Filled.DirectionsWalk, Icons.Default.DirectionsTransit
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        modes.forEachIndexed { index, mode ->
            val isSelected = selectedMode == mode
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF1976D2) else Color.LightGray)
                    .clickable { onModeSelected(mode) }
                    .padding(12.dp)) {

                Icon(
                    imageVector = icons[index],
                    contentDescription = mode,
                    tint = if (isSelected) Color.White else Color.Black
                )
                Text(
                    text = mode,
                    color = if (isSelected) Color.White else Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

