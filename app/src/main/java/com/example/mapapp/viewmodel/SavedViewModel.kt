package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapapp.KartoApplication
import com.example.mapapp.data.database.templates.TemplateWithStopCount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedViewModel(application : Application) : AndroidViewModel(application) {
    private val repository = (application as KartoApplication).templateRepository

    val savedTemplates: StateFlow<List<TemplateWithStopCount>> = repository.getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteTemplate(templateId: Int) {
        viewModelScope.launch {
            repository.deleteTemplateById(templateId)
        }
    }
}