package com.example.mapapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
    val locationInformation = vm.uiState.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.helsinki),
            contentDescription = "helsinki",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(340.dp)
        )

        BackButton(onClick = {
            navController.navigateUp()
        }, modifier = Modifier.padding(12.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 300.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(16.dp, 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                locationInformation.displayName?.let {
                    Text(text = it, style = MaterialTheme.typography.titleLarge)
                }
                locationInformation.type?.let { Text(text = it) }
            }
            locationInformation.summary?.let { Text(text = it) }
            locationInformation.rating?.let { RatingRow(it) }
        }
    }
}

@Composable
fun RatingRow(rating: Double) {
    @Composable
    fun getIcon(index: Int) {
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
            tint = Color.Unspecified
        )
    }

    if (rating <= 0.0 || rating > 5.0) {
        Text(text = "(No ratings)")
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            getIcon(1)
            getIcon(2)
            getIcon(3)
            getIcon(4)
            getIcon(5)
            Text(text = " ($rating)")
        }
    }
}