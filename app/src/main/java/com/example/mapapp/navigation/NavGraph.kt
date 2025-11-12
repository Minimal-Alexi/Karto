package com.example.mapapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapapp.ui.components.BottomBar
import com.example.mapapp.ui.screens.HomeScreen
import com.example.mapapp.ui.screens.ExploreScreen
import com.example.mapapp.ui.screens.LocationScreen
import com.example.mapapp.ui.screens.RouteScreen
import com.example.mapapp.ui.screens.SavedScreen
import com.example.mapapp.ui.screens.SettingsScreen

object Constants {
    const val LOCATION_SCREEN_ROUTE = "location/{locationID}"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navigateToLocationScreen: (String) -> Unit = { placesID ->
        navController.navigate(
            Constants.LOCATION_SCREEN_ROUTE.replace(
                "{locationID}",
                placesID
            )
        )
    }

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("explore") { ExploreScreen(navigateToLocationScreen = navigateToLocationScreen) }
            composable("route") { RouteScreen(navigateToLocationScreen = navigateToLocationScreen) }
            composable("saved") { SavedScreen() }
            composable("settings") { SettingsScreen() }

            composable("location/{locationID}") {
                val locationID = it.arguments?.getString("locationID")
                LocationScreen(locationID = locationID, navController = navController)
            }
        }
    }
}