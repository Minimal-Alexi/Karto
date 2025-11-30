package com.example.mapapp.data.database.template_stops

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_stops")
data class TemplateStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "template_id") val templateId: Int,
    val placesId: String,  // this is place unique identifier from the Places API
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val stayMinutes: Int,
    val position: Int,
    val typeOfPlace: String?
)