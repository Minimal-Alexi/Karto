package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.components.SavedRouteCard

@Composable
fun SavedScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Saved Routes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp)
        )

        val routes = listOf("Beaches of Helsinki", "Bars in Venice", "Museums in Paris") // this will fetch all saved routes

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SavedRouteCard(routes[0])
            SavedRouteCard(routes[1])
            SavedRouteCard(routes[2])
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}