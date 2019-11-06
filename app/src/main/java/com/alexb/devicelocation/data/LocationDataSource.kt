package com.alexb.devicelocation.data

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexb.devicelocation.di.Dependencies
import com.alexb.devicelocation.framework.location.LocationSupervisor
import com.alexb.devicelocation.framework.location.LocationUpdateSettings

class LocationDataSource(private val locationSupervisor: LocationSupervisor) {

    var lastLocation: Location? = null
    private val locationLiveData = MutableLiveData<Location>()

    fun getLocationLiveData(): LiveData<Location> = locationLiveData

    fun requestLastLocation() {
        locationSupervisor.requestLastLocation(::updateLocation)
    }

    fun startPeriodicUpdates(
        context: Context,
        settings: LocationUpdateSettings = LocationUpdateSettings.DEFAULT
    ) {
        locationSupervisor.startPeriodicUpdates(context, settings, ::updateLocation)
    }

    fun stopPeriodicUpdates() {
        locationSupervisor.stopPeriodicUpdates()
    }

    private fun updateLocation(location: Location?) {
        if (location != null) {
            logLocationUpdate(location)
            lastLocation = location
            locationLiveData.value = location
        }
    }

    private fun logLocationUpdate(location: Location) {
        val time = Dependencies.localTimeFormatter.format(location.time)
        val coordinates = "latitude = ${location.latitude}, longitude = ${location.longitude}"
        val accuracy = if (location.hasAccuracy()) ", accuracy = ${location.accuracy}" else ""
        Log.d(TAG, "[$time] $coordinates$accuracy")
    }

    companion object {
        private const val TAG = "LocationDataSource"
    }
}