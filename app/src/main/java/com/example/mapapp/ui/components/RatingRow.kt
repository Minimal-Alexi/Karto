package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.mapapp.R

@Composable
fun RatingRow(rating: Double, withLabel : Boolean = true) {
    @Composable
    fun getIcon(index: Int) {
        val painter = when {
            rating - index >= 0.0 -> painterResource(R.drawable.star_filled)
            rating - index >= -0.5 -> painterResource(R.drawable.star_half)
            else -> painterResource(R.drawable.star_empty)
        }

        val description = when {
            rating - index >= 0.0 -> "filled star icon"
            rating - index >= -0.5 -> "half star icon"
            else -> "empty star icon"
        }

        Icon(
            painter = painter,
            contentDescription = description,
            tint = Color.Unspecified
        )
    }

    if (rating <= 0.0 || rating > 5.0) {
        Text(text = "(No ratings)")
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            getIcon(1)
            getIcon(2)
            getIcon(3)
            getIcon(4)
            getIcon(5)
            if (withLabel) { Text(text = " ($rating)") }
        }
    }
}