package com.example.mapapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    const val SETTINGS_SCREEN_ROUTE = "settings"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    var currentRouteId: Int? = remember { null }

    val navigateToLocationScreen: (String) -> Unit = { placesId ->
        navController.navigate(
            Constants.LOCATION_SCREEN_ROUTE.replace(
                "{locationID}",
                placesId
            )
        )
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

    fun navigateToExplore(routeId: Int?) {
        val routeString = routeId?.toString() ?: ""
        navController.navigate("explore?routeId=$routeString") {
            launchSingleTop = true
        }
    }

    val openRouteFromSaved: (Int) -> Unit = { routeId ->
        currentRouteId = routeId
        navigateToExplore(routeId)
    }

    val resetRoute: () -> Unit = {
        currentRouteId = null
        navigateToExplore(null)
    }

    val navigateBottomBar: (String) -> Unit = { screen ->
        if (screen == "explore") {
            navigateToExplore(currentRouteId)
        } else {
            navController.navigate(screen) {
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController, navigateBottomBar) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable(
                route = "explore?routeId={routeId}",
                arguments = listOf(
                    navArgument("routeId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val routeId = backStackEntry.arguments?.getString("routeId")?.toIntOrNull()
                currentRouteId = routeId
                ExploreScreen(
                    navigateToLocationScreen = navigateToLocationScreen,
                    navigateToScreen = { navController.navigate(it) { launchSingleTop = true } },
                    openedRouteId = routeId,
                    onResetRoute = resetRoute
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