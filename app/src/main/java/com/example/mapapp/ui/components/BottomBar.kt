package com.example.mapapp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.res.painterResource
import com.example.mapapp.R

@Composable
fun BottomBar(navController: NavController, navigateBottomBar: (String) -> Unit) {
    val items = listOf(
        "home" to R.drawable.home_icon,
        "explore" to R.drawable.search_icon,
        "route" to R.drawable.route_icon,
        "saved" to R.drawable.saved_icon,
        "settings" to R.drawable.settings_icon
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { (screen, iconRes) ->
            val isSelected = currentRoute?.startsWith(screen) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = { navigateBottomBar(screen) },
                label = { Text(text = screen.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                icon = { Icon(painter = painterResource(id = iconRes), contentDescription = screen.replaceFirstChar { it.uppercase() }) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}