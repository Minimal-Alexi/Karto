package com.example.mapapp

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.mapapp.viewmodel.ThemeViewModel
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, requiredPermissions, 100)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        if (granted) {
            initializeApp()
        }
    }

    private fun initializeApp() {
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