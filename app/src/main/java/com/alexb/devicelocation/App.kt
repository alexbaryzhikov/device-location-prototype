package com.alexb.devicelocation

import android.app.Application
import com.alexb.devicelocation.di.Dependencies

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Dependencies.appContext = applicationContext
    }
}