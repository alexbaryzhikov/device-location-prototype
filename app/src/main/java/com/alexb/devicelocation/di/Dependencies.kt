package com.alexb.devicelocation.di

import android.content.Context
import com.alexb.devicelocation.data.LocationDataSource
import com.alexb.devicelocation.framework.location.LocationSupervisor
import com.alexb.devicelocation.utils.DateTimeUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import java.text.SimpleDateFormat

object Dependencies {

    lateinit var appContext: Context

    val locationDataSource: LocationDataSource by lazy {
        LocationDataSource(locationSupervisor)
    }

    val localTimeFormatter: SimpleDateFormat by lazy {
        dateTimeUtils.localTimeFormatter()
    }

    private val locationSupervisor: LocationSupervisor by lazy {
        LocationSupervisor(fusedLocationClient, locationSettingsClient)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    private val locationSettingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(appContext)
    }

    private val dateTimeUtils: DateTimeUtils by lazy {
        DateTimeUtils()
    }
}