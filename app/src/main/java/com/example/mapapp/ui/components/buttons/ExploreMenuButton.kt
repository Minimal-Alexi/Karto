package com.example.mapapp.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ExploreMenuButton(
    expandedState: MutableState<Boolean>,
    symbolWhenExpanded: ImageVector,
    symbolWhenClosed: ImageVector
) {
    Button(
        onClick = { expandedState.value = !expandedState.value },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(0.dp)
            .height(38.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
    ) {

        if (expandedState.value) {
            Icon(
                symbolWhenExpanded,
                contentDescription = "close menu icon",
            )
        } else {
            Icon(
                symbolWhenClosed,
                contentDescription = "expand menu icon",
            )
        }
    }
}