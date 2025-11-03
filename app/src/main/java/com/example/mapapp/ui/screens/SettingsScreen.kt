package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.viewmodel.SettingsViewModel
import com.example.mapapp.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen() {
    val vm = viewModel<SettingsViewModel>()
    val uiState by vm.uiState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        firstName = uiState.firstName ?: ""
        lastName = uiState.lastName ?: ""
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = firstName, fontSize = 20.sp)
            Button(onClick = { vm.updateFirstName("First${System.currentTimeMillis()}") }) {
                Text("first name update")
            }

            Text(text = lastName, fontSize = 20.sp)
            Button(onClick = { vm.updateLastName("Last${System.currentTimeMillis()}") }) {
                Text("last name update")
            }

            DarkModeSwitch()
        }
    }
}

@Composable
fun DarkModeSwitch() {
    val themeVM = viewModel<ThemeViewModel>()
    val isDarkTheme by themeVM.isDarkTheme.collectAsState()

    var checked = isDarkTheme

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            themeVM.setTheme(checked)
        }
    )
}