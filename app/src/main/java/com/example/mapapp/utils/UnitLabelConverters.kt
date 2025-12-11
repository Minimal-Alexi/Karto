package com.example.mapapp.utils

import com.example.mapapp.data.database.route_stops.RouteStopEntity
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

fun getTimeLabel(timeString: String?): String {
    if (timeString == null) {
        return ""
    }

    val timeValue = (timeString.replace("s", "")).toDoubleOrNull() ?: 0.0
    val hours = floor(timeValue / 3600).toInt()
    val minutes = ceil((timeValue % 3600) / 60).toInt()

    return if (hours > 0) {
        "$hours h $minutes min"
    } else {
        "$minutes min"
    }
}

fun getDistanceLabel(distanceString: Int?): String {
    val parsed = distanceString ?: return ""

    return if (parsed < 1000) {
        "$parsed m"
    } else {
        "${round(parsed / 1000.0 * 10) / 10} km"
    }
}

fun getTotalTimeLabel(stops: List<RouteStopEntity>?): String {
    if (stops.isNullOrEmpty()) return ""

    val totalSeconds = stops.sumOf { stop ->
        (stop.timeTo?.replace("s", "")?.toDoubleOrNull() ?: 0.0)
    }.toInt()

    val hours = totalSeconds / 3600
    val minutes = ceil((totalSeconds % 3600) / 60.0).toInt()

    return if (hours > 0) {
        "$hours h $minutes min"
    } else {
        "$minutes min"
    }
}

fun getTotalDistanceLabel(stops: List<RouteStopEntity>?): String {
    if (stops.isNullOrEmpty()) return ""

    val totalDistance = stops.sumOf { it.distanceTo ?: 0 } ?: 0

    return if (totalDistance >= 1000) {
        val km = totalDistance / 1000.0
        String.format("%.2f km", km)
    } else {
        "$totalDistance m"
    }
}