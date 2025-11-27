package com.example.mapapp.data.database.templates

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "saved_at") val savedAt: Long,
    @ColumnInfo(name = "starting_latitude") val startingLatitude: Double,
    @ColumnInfo(name = "starting_longitude") val startingLongitude: Double,
    @ColumnInfo(name = "category") val category: String? = null
)