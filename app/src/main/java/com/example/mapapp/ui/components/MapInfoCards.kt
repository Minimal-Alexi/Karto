package com.example.mapapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapapp.data.model.Place
import com.example.mapapp.ui.components.buttons.DeleteIconButton

@Composable
fun PlaceInfoCard(selectedPlace: Place, buttonOnClick: () -> Unit){
    Column(Modifier.padding(16.dp)) {
        Text(selectedPlace.displayName.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,)
        Text(selectedPlace.typeOfPlace?.displayName ?: "N/A",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))

        Button(onClick = {buttonOnClick()}) {
            Text("Add to route")
        }
    }
}

@Composable
fun RouteStopInfoCard(selectedPlace: Place, buttonOnClick: () -> Unit){
    Column(Modifier.padding(16.dp)) {
        Text(selectedPlace.displayName.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            )
        Text(selectedPlace.typeOfPlace?.displayName ?: "N/A",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))

        DeleteIconButton(buttonOnClick)
    }
}