package com.example.mapapp.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
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
    const val EXPLORE_SCREEN_ROUTE = "explore"
    const val ROUTE_SCREEN_ROUTE = "route"
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
        ) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToScreen: (String) -> Unit = { destination ->
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Special navigation for saved routes to force a new instance of ExploreScreen
    val openRouteFromSaved: (Int) -> Unit = { routeId ->
        navController.navigate("${Constants.EXPLORE_SCREEN_ROUTE}?routeId=$routeId") {
            launchSingleTop = false // ensure new instance with updated routeId
            restoreState = false
        }
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
            composable("explore?routeId={routeId}") { backStackEntry ->
                Log.d("NavGraph", "routeId: ${backStackEntry.arguments?.getString("routeId")}")
                val routeId = backStackEntry.arguments?.getString("routeId")?.toIntOrNull()
                ExploreScreen(
                    navigateToLocationScreen = navigateToLocationScreen,
                    navigateToScreen = navigateToScreen,
                    openedRouteId = routeId
                )
            }
            composable("route") {
                RouteScreen(
                    navigateToLocationScreen = navigateToLocationScreen,
                    navigateToScreen = navigateToScreen
                )
            }
            composable("saved") {
                SavedScreen(
                    onOpenRoute = openRouteFromSaved
                )
            }
            composable("settings") { SettingsScreen() }

            composable("location/{locationID}") {
                val locationID = it.arguments?.getString("locationID")
                LocationScreen(locationID = locationID, navController = navController)
            }
        }
    }
}