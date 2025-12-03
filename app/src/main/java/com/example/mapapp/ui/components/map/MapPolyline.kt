package com.example.mapapp.ui.components.map

import android.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberUpdatedMarkerState


@Composable
fun MapPolyline(
    polyline: MutableState<String?>,
    zoomLevel: Float
    ) {
    if (polyline.value != null) {
        val points = PolyUtil.decode(polyline.value)

        // 1. Calculate Arrow Positions and Bearings
        // We use remember(points) to avoid recalculating on every frame
        val arrowMarkers = remember(points, zoomLevel) {
            val markers = mutableListOf<Pair<LatLng, Float>>()

            // Dynamic interval logic:
            // At Zoom 15 (street level), arrows are close (e.g., 100m).
            // At Zoom 10 (city level), arrows are far apart (e.g., 2000m).
            // The formula: BaseDistance * (2 ^ (MaxZoom - CurrentZoom)) is a common mapping for map scales.
            // Simplified approach:
            val arrowIntervalMeters = when {
                zoomLevel >= 18f -> 50.0   // Very close
                zoomLevel >= 15f -> 200.0  // Street level
                zoomLevel >= 12f -> 1000.0 // Neighborhood
                zoomLevel >= 10f -> 5000.0 // City
                else -> 20000.0            // Country view
            }

            var distanceCovered = 0.0

            if (points.isNotEmpty()) {
                var prev = points[0]
                // Start slightly ahead so the first arrow isn't exactly on top of the start pin
                var nextMarkerDist = arrowIntervalMeters

                for (i in 1 until points.size) {
                    val current = points[i]
                    val segmentDist = SphericalUtil.computeDistanceBetween(prev, current)

                    // While the current segment contains the next marker point
                    while (distanceCovered + segmentDist >= nextMarkerDist) {
                        val fraction = (nextMarkerDist - distanceCovered) / segmentDist
                        val position = SphericalUtil.interpolate(prev, current, fraction)
                        val heading = SphericalUtil.computeHeading(prev, current)

                        markers.add(position to heading.toFloat())
                        nextMarkerDist += arrowIntervalMeters
                    }

                    distanceCovered += segmentDist
                    prev = current
                }
            }
            markers
        }

        Polyline(
            points = points,
            width = 10f,
            geodesic = true,
            endCap = CustomCap(
                BitmapDescriptorFactory.fromResource(R.drawable.arrow_up_float)
            ),
            spans = listOf(
                StyleSpan(
                    StrokeStyle.gradientBuilder(
                        MaterialTheme.colorScheme.primary.hashCode(),
                        MaterialTheme.colorScheme.secondary.hashCode()
                    ).build(), 1.0
                )
            )
        )

        // Render Start Marker
        if (points.isNotEmpty()) {
            Marker(
                state = rememberUpdatedMarkerState(position = points.first()),
                title = "Start",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }

        // Render the Arrows
        arrowMarkers.forEach { (location, bearing) ->
            Marker(
                state = rememberUpdatedMarkerState(position = location),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.arrow_up_float), // Use your arrow resource
                rotation = bearing, // Rotate to match road direction
                flat = true, // Makes the marker rotate with the map
                anchor = Offset(0.5f, 0.5f), // Center the arrow on the line
                onClick = { true } // Disable click events for these visual markers
            )
        }
    }
}