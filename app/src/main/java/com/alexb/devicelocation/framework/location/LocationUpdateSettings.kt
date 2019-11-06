package com.alexb.devicelocation.framework.location

import java.util.concurrent.TimeUnit

data class LocationUpdateSettings(
    val interval: Long = DEFAULT_LOCATION_UPDATE_INTERVAL,
    val maxWaitTime: Long = DEFAULT_LOCATION_MAX_WAIT_TIME,
    val priority: LocationRequestPriority = DEFAULT_LOCATION_REQUEST_PRIORITY
) {
    companion object {
        val DEFAULT_LOCATION_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(1)
        val DEFAULT_LOCATION_MAX_WAIT_TIME = TimeUnit.MINUTES.toMillis(10)
        val DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequestPriority.PRIORITY_HIGH_ACCURACY

        val DEFAULT: LocationUpdateSettings by lazy { LocationUpdateSettings() }
    }
}
