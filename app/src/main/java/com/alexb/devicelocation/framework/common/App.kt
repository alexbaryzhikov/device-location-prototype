package com.alexb.devicelocation.framework.common

import android.app.Application
import com.alexb.devicelocation.di.Dependencies

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Dependencies.appContext = applicationContext
    }
}
