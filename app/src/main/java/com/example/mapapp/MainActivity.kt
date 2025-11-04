package com.example.mapapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.navigation.NavGraph
import com.example.mapapp.ui.theme.MapAppTheme
import com.example.mapapp.utils.SecretsHolder
import com.google.android.libraries.places.api.Places
import com.example.mapapp.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, SecretsHolder.apiKey!!)
        }
        enableEdgeToEdge()
        setContent {
            AppWrapper()
        }
    }
}

@Composable
fun AppWrapper() {
    val application = LocalContext.current.applicationContext as KartoApplication
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModel.Factory(application))
    val useDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    MapAppTheme(
        darkTheme = useDarkTheme
    ) {
        NavGraph()
    }
}