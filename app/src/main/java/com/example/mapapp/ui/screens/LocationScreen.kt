package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapapp.ui.components.BackButton
import com.example.mapapp.viewmodel.LocationScreenViewModel

@Composable
fun LocationScreen(locationID: String?, navController: NavController) {
    /* TODO: actual error screen */
    if (locationID == null) {
        BackButton(onClick = { navController.navigateUp() })

        Text("error: No location ID provided")
    } else {
        LocationDetailsScreen(locationID, navController)
    }
}

@Composable
fun LocationDetailsScreen(locationID: String, navController: NavController) {
    val vm = viewModel<LocationScreenViewModel>()

    Column {
        BackButton(onClick = { navController.navigateUp() })

        Button(onClick = { vm.getLocationInformation(locationID) }) {
            Text("test")
        }

        Text("Looking for location with ID: $locationID")
    }
}