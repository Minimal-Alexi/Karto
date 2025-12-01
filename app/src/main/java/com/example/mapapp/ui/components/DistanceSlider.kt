package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants
import kotlin.math.round


@Composable
fun DistanceSlider(
    distanceValue: Double,
    onDistanceChange: (Double) -> Unit
) {
    val stepSize = 100.0  // interval in km
    val range = 500f..10000f

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("within the distance of", style = MaterialTheme.typography.bodyMedium)
            Text(text = "%.1f km".format(distanceValue / 1000))
        }
        Slider(
            value = distanceValue.toFloat(),
            onValueChange = {
                val snapped = (round(it / stepSize.toFloat()) * stepSize).coerceIn(
                    range.start.toDouble(),
                    range.endInclusive.toDouble()
                )
                onDistanceChange(snapped)
            },
            valueRange = range,
            steps = ((range.endInclusive - range.start) / stepSize.toFloat()).toInt() - 1
        )

    }
}