package com.example.mapapp.ui.components.route

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapapp.viewmodel.ExploreViewModel
import com.example.mapapp.viewmodel.PredictionViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest

@Composable
fun StartingLocationSelector() {
    val predictionViewModel = viewModel<PredictionViewModel>()

    val exploreViewModel = viewModel<ExploreViewModel>()

    var locationFieldText by remember { mutableStateOf("") }

    val predictions by predictionViewModel.originPredictions.collectAsState()

    fun setLocationFieldText(str : String) {
        locationFieldText = str
    }

    Text("starting from")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = locationFieldText,
            onValueChange = {
                predictionViewModel.clearPredictionsForOrigin()
                locationFieldText = it
                if (it.length > 2) {
                    predictionViewModel.searchPlacesForOrigin(it)
                } else {
                    exploreViewModel.nullCustomLocation()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Your Current Location") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Location icon"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (predictions.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 180),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = { predictionViewModel.clearPredictionsForOrigin() }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        predictions.forEach { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        predictionViewModel.fetchPlace(
                                            prediction.placeId,
                                            exploreViewModel::setOriginLocation,
                                            exploreViewModel::setCustomLocationText,
                                            ::setLocationFieldText
                                        )
                                    }
                                    .padding(12.dp)
                            )

                            HorizontalDivider(color = Color(0xFFDDDDDD))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}