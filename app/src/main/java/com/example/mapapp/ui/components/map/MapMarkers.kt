package com.example.mapapp.ui.components.map

import androidx.compose.runtime.Composable
import com.example.mapapp.data.model.Place
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun UserMarker(userLocation: LatLng) {
    return Marker(
        state = rememberUpdatedMarkerState(position = userLocation),
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
        title = "Your location",
        snippet = "Your current location"
    )
}

@Composable
fun PlaceMarker(place: Place){
    return Marker(
        state = rememberUpdatedMarkerState(position = place.location),
        title = place.displayName.text,
        icon = BitmapDescriptorFactory.defaultMarker(
            place.typeOfPlace?.markerColor ?: BitmapDescriptorFactory.HUE_RED
        ),
        tag = place,
    )
}