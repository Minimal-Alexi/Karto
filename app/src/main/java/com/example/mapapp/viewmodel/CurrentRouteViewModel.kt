package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CurrentRouteViewModel(application: Application) : AndroidViewModel(application) {
    val isOnRoute = false // TODO: pull from database
}