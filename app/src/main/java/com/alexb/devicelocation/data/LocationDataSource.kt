package com.alexb.devicelocation.data

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexb.devicelocation.di.Dependencies
import com.alexb.devicelocation.framework.location.LocationRequestPriority
import com.alexb.devicelocation.framework.location.LocationSupervisor
import com.alexb.devicelocation.framework.location.LocationSupervisor.Companion.DEFAULT_LOCATION_REQUEST_PRIORITY
import com.alexb.devicelocation.framework.location.LocationSupervisor.Companion.DEFAULT_LOCATION_UPDATE_INTERVAL

class LocationDataSource(private val locationSupervisor: LocationSupervisor) {

    var lastLocation: Location? = null
    private val locationLiveData = MutableLiveData<Location>()

    fun getLocationLiveData(): LiveData<Location> = locationLiveData

    fun requestLastLocation() {
        locationSupervisor.requestLastLocation(::updateLocation)
    }

    fun activatePeriodicUpdates(
        interval: Long = DEFAULT_LOCATION_UPDATE_INTERVAL,
        priority: LocationRequestPriority = DEFAULT_LOCATION_REQUEST_PRIORITY
    ) {
        locationSupervisor.activatePeriodicUpdates(interval, priority, ::updateLocation)
    }

    private fun updateLocation(location: Location) {
        logLocationUpdate(location)
        lastLocation = location
        locationLiveData.value = location
    }

    private fun logLocationUpdate(location: Location) {
        val lat = location.latitude
        val lon = location.longitude
        val time = Dependencies.localTimeFormatter.format(location.time)
        Log.d(TAG, "Location: lat = $lat, lon = $lon, update time = $time")
    }

    companion object {
        private const val TAG = "LocationDataSource"
    }
}