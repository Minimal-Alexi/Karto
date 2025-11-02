package com.example.mapapp.utils

import android.app.Application
import android.content.pm.PackageManager

class CredentialManager: Application() {
    lateinit var googleApi: String
    override fun onCreate() {
        super.onCreate()
        val appInfo = applicationContext.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        googleApi = appInfo.metaData.getString("com.google.android.geo.API_KEY").toString()
    }
}