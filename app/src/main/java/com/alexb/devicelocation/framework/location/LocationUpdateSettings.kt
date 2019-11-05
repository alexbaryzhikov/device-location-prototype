package com.alexb.devicelocation.framework.location

import com.alexb.devicelocation.framework.location.LocationSupervisor.Companion.DEFAULT_LOCATION_MAX_WAIT_TIME
import com.alexb.devicelocation.framework.location.LocationSupervisor.Companion.DEFAULT_LOCATION_REQUEST_PRIORITY
import com.alexb.devicelocation.framework.location.LocationSupervisor.Companion.DEFAULT_LOCATION_UPDATE_INTERVAL

data class LocationUpdateSettings(
    val interval: Long = DEFAULT_LOCATION_UPDATE_INTERVAL,
    val maxWaitTime: Long = DEFAULT_LOCATION_MAX_WAIT_TIME,
    val priority: LocationRequestPriority = DEFAULT_LOCATION_REQUEST_PRIORITY
) {
    companion object {
        val DEFAULT: LocationUpdateSettings by lazy { LocationUpdateSettings() }
    }
}
