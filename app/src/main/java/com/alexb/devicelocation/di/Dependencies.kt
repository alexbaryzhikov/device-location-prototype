package com.alexb.devicelocation.di

import android.content.Context
import com.alexb.devicelocation.data.LocationDataSource
import com.alexb.devicelocation.framework.location.LocationSupervisor
import com.alexb.devicelocation.utils.DateTimeUtils
import com.google.android.gms.location.LocationServices
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
        LocationSupervisor(fusedLocationClient)
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(appContext)
    }

    private val dateTimeUtils: DateTimeUtils by lazy {
        DateTimeUtils()
    }
}