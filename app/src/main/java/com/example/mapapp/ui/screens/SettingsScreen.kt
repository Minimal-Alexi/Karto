package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.ui.components.ConfirmationModal
import com.example.mapapp.ui.components.HistoryRouteCard
import com.example.mapapp.ui.components.Placeholder
import com.example.mapapp.utils.DialogData
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
                .fillMaxSize()
                .padding(16.dp, 0.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Profile()
            Settings()
            RouteHistory()
        }
    }
}

@Composable
fun Profile() {
    val vm = viewModel<SettingsViewModel>()
    val user by vm.user.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val isDataChanged = (firstName != user?.firstName) || (lastName != user?.lastName)

    LaunchedEffect(user) {
        firstName = user?.firstName ?: ""
        lastName = user?.lastName ?: ""
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
    val vm = viewModel<SettingsViewModel>()
    val routes = vm.completedRoutes.collectAsState().value

    val dialogDataState = vm.dialogDataState

    ConfirmationModal(dialogDataState.value)

    Text(
        "Route History", style = MaterialTheme.typography.titleLarge
    )

    val errorColor = MaterialTheme.colorScheme.error
    if (routes.isNotEmpty()) {
        Column {
            for (route in routes) {
                HistoryRouteCard(route, onDelete = {
                    dialogDataState.value = DialogData(
                        title = "Delete this Route from your History?",
                        text = "If you delete it, it's gone forever.",
                        confirmLabel = "Delete",
                        onConfirm = { vm.deleteRouteById(route.id) },
                        confirmColor = errorColor,
                        isShowing = mutableStateOf(true)
                    )
                })
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        Placeholder("You don't have route history yet. When you start a route, it will appear here.")
    }
}