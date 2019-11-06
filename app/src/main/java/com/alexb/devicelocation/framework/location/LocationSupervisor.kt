package com.alexb.devicelocation.framework.location

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.*

class LocationSupervisor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient
) {

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Exception in coroutine scope", throwable)
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + handler)
    private var startUpdatesJob: Job? = null
    private var settingsException: ResolvableApiException? = null
    private var settingsResolutionResult: CompletableDeferred<Boolean>? = null
    private var settingsResolved = false
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
        context: Context,
        settings: LocationUpdateSettings,
        updateLocation: (Location) -> Unit
    ) {
        startOrReplaceUpdatesJob {
            stopPeriodicUpdates()
            val locationRequest = locationRequest(settings)
            resolveSettings(context, locationRequest)
            startUpdates(locationRequest, updateLocation)
        }
    }

    fun stopPeriodicUpdates() {
        val callback = locationCallback
        if (callback != null) {
            Log.d(TAG, "Stop periodic updates")
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    fun startSettingsResolution(activity: Activity) {
        runCatching {
            settingsException?.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
        }
    }

    fun onSettingsResolved() {
        Log.d(TAG, "Location settings resolved")
        settingsResolutionResult?.complete(true)
    }

    fun onSettingsResolutionFailed() {
        Log.e(TAG, "Failed to resolve location settings")
        settingsResolutionResult?.complete(false)
    }

    private fun startOrReplaceUpdatesJob(block: suspend CoroutineScope.() -> Unit) {
        startUpdatesJob?.cancel()
        startUpdatesJob = scope.launch { block() }
    }

    private fun locationRequest(settings: LocationUpdateSettings): LocationRequest {
        return LocationRequest.create().apply {
            interval = settings.interval
            priority = settings.priority.id
            maxWaitTime = settings.maxWaitTime
        }
    }

    private suspend fun resolveSettings(context: Context, locationRequest: LocationRequest) {
        val locationSettingsRequest = settingsRequest(locationRequest)
        settingsResolutionResult = CompletableDeferred()
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                settingsResolutionResult?.complete(true)
            }
            .addOnFailureListener { exception ->
                promptUserToChangeSettings(context, exception)
            }
        settingsResolutionResult?.await()?.let { result ->
            settingsResolved = result
        }
    }

    private fun settingsRequest(locationRequest: LocationRequest): LocationSettingsRequest {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        return builder.build()
    }

    private fun promptUserToChangeSettings(context: Context, exception: Exception) {
        Log.e(TAG, "Location settings are not satisfied: $exception")
        if (exception is ResolvableApiException) {
            settingsException = exception
            SettingsResolutionActivity.start(context)
        }
    }

    private fun startUpdates(
        locationRequest: LocationRequest,
        updateLocation: (Location) -> Unit
    ) {
        if (settingsResolved) {
            Log.d(
                TAG, "Start periodic updates" +
                        ", interval = ${locationRequest.interval}" +
                        ", maxWaitTime = ${locationRequest.maxWaitTime}" +
                        ", priority = ${locationRequest.priority}"
            )
            locationCallback = locationCallback(updateLocation)
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
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
        const val REQUEST_CHECK_SETTINGS = 100
        private const val TAG = "LocationSupervisor"
    }
}