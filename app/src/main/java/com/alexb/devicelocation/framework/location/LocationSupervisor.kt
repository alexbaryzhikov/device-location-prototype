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

    fun requestLastLocation(updateLocation: (Location) -> Unit) {
        runCatching {
            fusedLocationClient.lastLocation.addOnSuccessListener(updateLocation)
        }.onFailure {
            Log.e(TAG, "Failed to request last location", it)
        }.onSuccess {
            Log.d(TAG, "Last location requested")
        }
    }

    fun activatePeriodicUpdates(
        interval: Long,
        priority: LocationRequestPriority,
        updateLocation: (Location) -> Unit
    ) {
        val locationRequest = locationRequest(interval, priority)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        settingsClient.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener { response ->
                Log.d(TAG, "Location response: $response")
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback(updateLocation),
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

    private fun locationRequest(
        interval: Long,
        priority: LocationRequestPriority
    ): LocationRequest {
        return LocationRequest.create().apply {
            this.interval = interval
            this.priority = priority.id
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
        val DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequestPriority.PRIORITY_HIGH_ACCURACY

        private const val TAG = "LocationSupervisor"
    }
}