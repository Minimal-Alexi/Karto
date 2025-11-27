package com.example.mapapp.data.database.template_stops

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface TemplateStopDao {
    @Insert
    suspend fun insert(stop: TemplateStopEntity)

    @Update
    suspend fun updateTemplateStop(templateStop: TemplateStopEntity)

    @Query("SELECT * FROM template_stops WHERE template_id = :templateId")
    suspend fun getStopsForTemplate(templateId: Int): List<TemplateStopEntity>

    @Query("DELETE FROM template_stops WHERE template_id = :templateId")
    suspend fun deleteStopsByTemplate(templateId: Int)
}