package com.example.mapapp.navigation

import androidx.compose.foundation.layout.PaddingValues
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
import com.example.mapapp.ui.screens.RouteScreen
import com.example.mapapp.ui.screens.SavedScreen
import com.example.mapapp.ui.screens.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("explore") { ExploreScreen() }
            composable("route") { RouteScreen() }
            composable("saved") { SavedScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}