package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.ui.components.SavedRouteCard
import com.example.mapapp.viewmodel.SavedViewModel

@Composable
fun SavedScreen() {
    val savedViewModel = viewModel<SavedViewModel>()
    val routes by savedViewModel.allRoutes.collectAsState()

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

        routes.forEach { route ->
            SavedRouteCard(
                route = route,
                onDelete = { savedViewModel.deleteRoute(route) }
            )
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}