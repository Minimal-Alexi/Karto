package com.example.mapapp.utils

object SecretsHolder {
    var apiKey:String? = null
        private set

    fun init(value:String?){
        apiKey = value
    }
}