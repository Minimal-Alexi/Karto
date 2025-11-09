package com.example.mapapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapapp.ui.components.BackButton
import com.example.mapapp.viewmodel.LocationScreenViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mapapp.R

@Composable
fun LocationScreen(locationID: String?, navController: NavController) {
    if (locationID == null) {
        BackButton(onClick = { navController.navigateUp() })
        Text("error: No location ID provided")
    } else {
        LocationDetailsScreen(locationID, navController)
    }
}

@Composable
fun LocationDetailsScreen(locationID: String, navController: NavController) {
    val vm = viewModel<LocationScreenViewModel>()
    vm.getLocationInformation(locationID)
    // vm.setUIStateWithoutFetch()
    val locationInformation = vm.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackButton(onClick = {
            navController.navigateUp()
            vm.clearUIState()
        })

        if (locationInformation.displayName != null) {
            Text(
                text = locationInformation.displayName,
                style = MaterialTheme.typography.titleLarge
            )
        }

        locationInformation.summary?.let{ Text(text = it) }
        locationInformation.rating?.let { RatingRow(it) }
    }
}

@Composable
fun RatingRow(rating: Double) {
    @Composable
    fun getIcon(index : Int) {
        val painter = when {
            rating - index >= 0.0 -> painterResource(R.drawable.star_filled)
            rating - index >= -0.5 -> painterResource(R.drawable.star_half)
            else -> painterResource(R.drawable.star_empty)
        }

        val description = when {
            rating - index >= 0.0 -> "filled star icon"
            rating - index >= -0.5 -> "half star icon"
            else -> "empty star icon"
        }

        Icon(
            painter = painter,
            contentDescription = description,
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
    }

    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        getIcon(1)
        getIcon(2)
        getIcon(3)
        getIcon(4)
        getIcon(5)
        Text(text = " ($rating)")
    }
}