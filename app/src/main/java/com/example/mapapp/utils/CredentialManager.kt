package com.example.mapapp.utils

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log

class CredentialManager : Application() {
    override fun onCreate() {
        super.onCreate()
        val appInfo = applicationContext.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
        SecretsHolder.init(apiKey)
    }
}