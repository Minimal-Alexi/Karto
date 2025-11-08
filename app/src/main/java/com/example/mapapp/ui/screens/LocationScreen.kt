package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.mapapp.ui.components.BackButton

@Composable
fun LocationScreen(locationID: String?, navController: NavController) {
    /* TODO: actual error screen */
    if (locationID == null) {
        BackButton(onClick = { navController.navigateUp() })

        Text("error: Invalid location ID")
    } else {
        Column {
            BackButton(onClick = { navController.navigateUp() })

            Text("Looking for location with ID: $locationID")
        }
    }
}