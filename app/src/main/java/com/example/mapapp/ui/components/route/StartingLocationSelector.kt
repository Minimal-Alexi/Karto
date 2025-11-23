package com.example.mapapp.ui.components.route

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    var locationText by remember { mutableStateOf("") }
    val predictions by predictionViewModel.originPredictions.collectAsState()

    Text("starting from")

    OutlinedTextField(
        value = locationText,
        onValueChange = {
            predictionViewModel.clearPredictionsForOrigin()
            locationText = it
            if (it.length > 2) predictionViewModel.searchPlacesForOrigin(it)
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

    predictions.forEach { prediction ->
        Text(
            text = prediction.getFullText(null).toString(),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val placeId = prediction.placeId
                    val placeFields = listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME)
                    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                    predictionViewModel.placesClient.fetchPlace(request)
                        .addOnSuccessListener { response ->
                            val place = response.place
                            exploreViewModel.setLocation(
                                LatLng(place.location!!.latitude,
                                place.location!!.longitude)
                            )
                            locationText = place.formattedAddress ?: place.displayName
                                    ?: ""
                            predictionViewModel.clearPredictionsForOrigin()
                        }
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))
    }
}