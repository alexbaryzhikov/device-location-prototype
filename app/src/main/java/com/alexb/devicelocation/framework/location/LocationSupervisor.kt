package com.alexb.devicelocation.framework.location

import android.location.Location
import android.os.Looper
import android.util.Log
import com.alexb.devicelocation.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class LocationSupervisor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient
) {

    private var locationCallback: LocationCallback? = null

    fun requestLastLocation(updateLocation: (Location) -> Unit) {
        runCatching {
            fusedLocationClient.lastLocation.addOnSuccessListener(updateLocation)
        }.onFailure {
            Log.e(TAG, "Failed to request last location", it)
        }.onSuccess {
            Log.d(TAG, "Last location requested")
        }
    }

    fun startPeriodicUpdates(
        settings: LocationUpdateSettings,
        updateLocation: (Location) -> Unit
    ) {
        stopPeriodicUpdates()
        val locationRequest = locationRequest(settings)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        settingsClient.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener {
                Log.d(TAG, "Start periodic updates")
                locationCallback = locationCallback(updateLocation)
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            addOnFailureListener { exception ->
                Log.e(TAG, "Location settings are not satisfied: $exception")
                if (exception is ResolvableApiException) {
                    runCatching {
                        exception.startResolutionForResult(
                            MainActivity.instance,
                            REQUEST_CHECK_SETTINGS
                        )
                    }
                }
            }
        }
    }

    fun stopPeriodicUpdates() {
        val callback = locationCallback
        if (callback != null) {
            Log.d(TAG, "Stop periodic updates")
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun locationRequest(settings: LocationUpdateSettings): LocationRequest {
        return LocationRequest.create().apply {
            interval = settings.interval
            priority = settings.priority.id
            maxWaitTime = settings.maxWaitTime
        }
    }

    private fun locationCallback(updateLocation: (Location) -> Unit): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.locations?.forEach(updateLocation)
            }
        }
    }

    companion object {

        const val REQUEST_CHECK_SETTINGS = 23

        val DEFAULT_LOCATION_UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(5)
        val DEFAULT_LOCATION_MAX_WAIT_TIME = TimeUnit.SECONDS.toMillis(20)
        val DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequestPriority.PRIORITY_HIGH_ACCURACY

        private const val TAG = "LocationSupervisor"
    }
}