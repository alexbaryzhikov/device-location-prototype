package com.alexb.devicelocation.di

import android.content.Context
import com.alexb.devicelocation.data.LocationDataSource
import com.alexb.devicelocation.data.SharedPreferencesDataSource
import com.alexb.devicelocation.framework.location.LocationSupervisor
import com.alexb.devicelocation.framework.logging.LogWriter
import com.alexb.devicelocation.framework.preferences.SharedPreferencesProxy
import com.alexb.devicelocation.utils.DateTimeUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import java.text.SimpleDateFormat

object Dependencies {

    lateinit var appContext: Context

    val locationDataSource: LocationDataSource by lazy {
        LocationDataSource(locationSupervisor, logWriter)
    }

    val locationSupervisor: LocationSupervisor by lazy {
        LocationSupervisor(fusedLocationClient, locationSettingsClient)
    }

    val localTimeFormatter: SimpleDateFormat by lazy {
        dateTimeUtils.localTimeFormatter()
    }

    val sharedPreferencesDataSource: SharedPreferencesDataSource by lazy {
        SharedPreferencesDataSource(sharedPreferencesProxy)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    private val locationSettingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(appContext)
    }

    private val logWriter: LogWriter by lazy {
        LogWriter(appContext)
    }

    private val dateTimeUtils: DateTimeUtils by lazy {
        DateTimeUtils()
    }

    private val sharedPreferencesProxy: SharedPreferencesProxy by lazy {
        SharedPreferencesProxy(appContext)
    }
}