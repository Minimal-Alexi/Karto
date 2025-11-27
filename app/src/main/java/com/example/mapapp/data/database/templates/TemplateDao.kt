package com.example.mapapp.data.database.templates

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity): Long

    @Query("SELECT * FROM templates WHERE id = :templateId LIMIT 1")
    suspend fun getTemplateById(templateId: Int): TemplateEntity?

    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteTemplateById(templateId: Int)

    @Update
    suspend fun updateTemplate(route: TemplateEntity)

    @Query("""
        SELECT r.id AS id,
               r.title AS title,
               r.saved_at AS savedAt,
               r.starting_latitude AS startingLatitude,
               r.starting_longitude AS startingLongitude,
               r.category AS category,
               COUNT(s.id) AS stopsCount
        FROM templates AS r
        LEFT JOIN template_stops AS s ON r.id = s.template_id
        GROUP BY r.id
    """)
    fun getAllTemplates(): Flow<List<TemplateWithStopCount>>
}