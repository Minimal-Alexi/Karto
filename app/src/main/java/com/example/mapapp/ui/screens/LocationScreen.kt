package com.example.mapapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapapp.ui.components.BackButton
import com.example.mapapp.viewmodel.LocationScreenViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.mapapp.ui.components.RatingRow

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

    LaunchedEffect(key1 = locationID) {
        vm.getLocationInformation(locationID)
    }

    val locationInformation = vm.uiState.collectAsState().value

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val containerHeight = this.maxHeight

        Box(modifier = Modifier.fillMaxSize()) {
            if (locationInformation.photo != null) {
                Image(
                    bitmap = locationInformation.photo.asImageBitmap(),
                    contentDescription = "helsinki",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = containerHeight / 2)
                )

                BackButton(onClick = {
                    navController.navigateUp()
                }, modifier = Modifier.padding(12.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (locationInformation.photo != null) {
                            (containerHeight / 2) - 40.dp
                        } else {
                            0.dp
                        }
                    )
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(16.dp, 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    locationInformation.displayName?.let { it ->
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            if (locationInformation.photo == null) {
                                BackButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(0.dp))
                            }

                            Text(text = it, style = MaterialTheme.typography.titleLarge)
                        }
                        locationInformation.type?.let { Text(text = it) }
                    }
                    locationInformation.summary?.let { Text(text = it) }
                    locationInformation.rating?.let { RatingRow(it) }
                }
            }
        }
    }
}