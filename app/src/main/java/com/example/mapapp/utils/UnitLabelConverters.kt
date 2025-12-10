package com.example.mapapp.utils

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

fun getDistanceLabel(distanceString: String?): String {
    val parsed = distanceString?.toIntOrNull() ?: return ""

    return if (parsed < 1000) {
        "$parsed m"
    } else {
        "${round(parsed / 1000.0 * 10) / 10} km"
    }
}