package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication: Application() {
    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context: Context
        const val TOKEN = "OapSUmHd0SS1g4JC"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}