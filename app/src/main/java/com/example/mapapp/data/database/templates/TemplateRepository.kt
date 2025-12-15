package com.example.mapapp.data.database.templates

import com.example.mapapp.data.database.template_stops.TemplateStopDao
import com.example.mapapp.data.database.template_stops.TemplateStopEntity
import kotlinx.coroutines.flow.Flow


class TemplateRepository(
    private val templateDao: TemplateDao,
    private val templateStopDao: TemplateStopDao
) {
    fun getAllTemplates(): Flow<List<TemplateWithStopCount>> = templateDao.getAllTemplates()

    suspend fun getTemplateWithStops(templateId: Int): TemplateWithStops {
        val template = templateDao.getTemplateById(templateId)
            ?: throw IllegalStateException("Template with id $templateId does not exist")
        val stops = templateStopDao.getStopsForTemplate(templateId)
        return TemplateWithStops(template, stops)}

    suspend fun updateTemplateStop(templateStopEntity: TemplateStopEntity){
        templateStopDao.updateTemplateStop(templateStopEntity)
    }

    suspend fun saveTemplate(template: TemplateEntity, stops: List<TemplateStopEntity>) {
        val id = templateDao.insertTemplate(template.copy()).toInt()

        stops.forEach { stop ->
            templateStopDao.insert(
                stop.copy(templateId = id)
            )
        }
    }

    suspend fun deleteTemplateById(templateId: Int) {
        templateDao.deleteTemplateById(templateId)
        templateStopDao.deleteStopsByTemplate(templateId)
    }

    suspend fun updateTemplate(template: TemplateEntity, stops: List<TemplateStopEntity>) {
        templateDao.updateTemplate(template)
        templateStopDao.deleteStopsByTemplate(template.id)
        stops.forEach { stop ->
            templateStopDao.insert(stop.copy(templateId = template.id))
        }
    }

    suspend fun templateExists(templateId: Int): Boolean {
        return templateDao.getTemplateById(templateId) != null
    }
}

data class TemplateWithStops(
    val route: TemplateEntity,
    val stops: List<TemplateStopEntity> = emptyList()
)

data class TemplateWithStopCount(
    val id: Int,
    val title: String,
    val savedAt: Long,
    val startingLatitude: Double,
    val startingLongitude: Double,
    val category: String?,
    val stopsCount: Int
)