package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun DistanceSlider(distanceValue:Double, onDistanceChange: (Double) -> Unit){
    Column {
        Text("within total walking distance of", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = distanceValue.toFloat(),
            onValueChange = {onDistanceChange(it.toDouble())},
            valueRange = 500f..10000f,
            steps = ((10000f - 500f) / 100f).toInt() - 1
        )
    }
}