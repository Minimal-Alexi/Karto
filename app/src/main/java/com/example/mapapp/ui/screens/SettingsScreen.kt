package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mapapp.ui.components.SavedRouteCard
import com.example.mapapp.viewmodel.SettingsViewModel
import com.example.mapapp.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Profile()
            Settings()
            RouteHistory()
        }
    }
}

@Composable
fun Profile() {
    val vm = viewModel<SettingsViewModel>()
    val uiState by vm.uiState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val isDataChanged = (firstName != uiState.firstName) || (lastName != uiState.lastName)

    LaunchedEffect(uiState) {
        firstName = uiState.firstName ?: ""
        lastName = uiState.lastName ?: ""
    }
    Text("Profile Name", style = MaterialTheme.typography.titleLarge)

    OutlinedTextField(
        value = firstName,
        onValueChange = { firstName = it }, singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("First Name") }
    )

    OutlinedTextField(
        value = lastName,
        onValueChange = { lastName = it }, singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Last Name") }
    )

    Button(
        onClick = { vm.updateName(firstName, lastName) },
        modifier = Modifier.fillMaxWidth(),
        enabled = isDataChanged
    ) {
        Text(text = "Update Name", modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun Settings() {
    Text("Settings", style = MaterialTheme.typography.titleLarge)

    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Dark Mode", style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )

        DarkModeSwitch()
    }
}

@Composable
fun DarkModeSwitch() {
    val themeVM = viewModel<ThemeViewModel>()
    val isDarkTheme by themeVM.isDarkTheme.collectAsState()

    Switch(
        checked = isDarkTheme,
        onCheckedChange = { isChecked ->
            themeVM.setTheme(isChecked)
        }
    )
}

@Composable
fun RouteHistory() {
    val routes = listOf("route 1", "route 2", "route 3") // this will fetch last 3 from user later

    Row {
        Text(
            "History", style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}, modifier = Modifier.padding(0.dp)) {
            Text("View All")
        }
    }

    Column {
        SavedRouteCard(routes[0])
        SavedRouteCard(routes[1])
        SavedRouteCard(routes[2])
        Spacer(modifier = Modifier.height(16.dp))
    }
}