package com.alexb.devicelocation.framework.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient

class LocationSupervisor(private val fusedLocationClient: FusedLocationProviderClient) {

    fun receiveLastLocation(updateLocation: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener(updateLocation)
    }

    companion object {
        private const val TAG = "LocationSupervisor"
    }
}