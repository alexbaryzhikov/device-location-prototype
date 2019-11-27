package com.alexb.devicelocation.framework.common

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alexb.devicelocation.R
import com.alexb.devicelocation.di.Dependencies
import com.alexb.devicelocation.framework.location.LocationUpdateSettings
import com.alexb.devicelocation.framework.location.LocationUpdateSettings.Companion.DEFAULT_LOCATION_MAX_WAIT_TIME
import com.alexb.devicelocation.framework.location.LocationUpdateSettings.Companion.DEFAULT_LOCATION_UPDATE_INTERVAL
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val locationDataSource by lazy { Dependencies.locationDataSource }
    private val sharedPreferencesDataSource by lazy { Dependencies.sharedPreferencesDataSource }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        satisfyPermissions()

        setupLastLocationButton()
        setupDisplayOnMapButton()
        setupStartPeriodicUpdatesButton()
        setupStopPeriodicUpdatesButton()
        setupStartServiceButton()
        setupStopServiceButton()

        setupLocationRendering()
    }

    private fun satisfyPermissions() {
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val permissions = packageInfo.requestedPermissions ?: return
        val satisfied = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!satisfied) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val deniedPermissions = permissions.filterIndexed { i, _ ->
            grantResults[i] == PackageManager.PERMISSION_DENIED
        }
        if (deniedPermissions.isEmpty()) {
            Log.d(TAG, "Permissions granted")
        } else {
            Log.e(TAG, "Denied permissions: $deniedPermissions")
        }
    }

    override fun onStart() {
        super.onStart()
        setupIntervalEditText()
        setupMaxWaitTimeEditText()
    }

    override fun onStop() {
        saveInterval()
        saveMaxWaitTime()
        super.onStop()
    }

    private fun saveInterval() {
        val interval = intervalEditText.text.toString()
        sharedPreferencesDataSource.save(UPDATE_INTERVAL_KEY, interval)
    }

    private fun saveMaxWaitTime() {
        val maxWaitTime = maxWaitTimeEditText.text.toString()
        sharedPreferencesDataSource.save(MAX_WAIT_TIME_KEY, maxWaitTime)
    }

    private fun setupIntervalEditText() {
        val default = TimeUnit.MILLISECONDS.toSeconds(DEFAULT_LOCATION_UPDATE_INTERVAL).toString()
        val interval = sharedPreferencesDataSource.load(UPDATE_INTERVAL_KEY, default)
        intervalEditText.setText(interval, TextView.BufferType.EDITABLE)
    }

    private fun setupMaxWaitTimeEditText() {
        val default = TimeUnit.MILLISECONDS.toSeconds(DEFAULT_LOCATION_MAX_WAIT_TIME).toString()
        val maxWaitTime = sharedPreferencesDataSource.load(MAX_WAIT_TIME_KEY, default)
        maxWaitTimeEditText.setText(maxWaitTime, TextView.BufferType.EDITABLE)
    }

    private fun setupLastLocationButton() {
        lastLocationButton.setOnClickListener {
            locationDataSource.requestLastLocation()
        }
    }

    private fun setupStartPeriodicUpdatesButton() {
        startPeriodicUpdatesButton.setOnClickListener {
            locationDataSource.startPeriodicUpdates(this, settings())
        }
    }

    private fun settings(): LocationUpdateSettings {
        val interval = interval()
        val maxWaitTime = maxWaitTime()
        return LocationUpdateSettings(interval = interval, maxWaitTime = maxWaitTime)
    }

    private fun interval(): Long {
        return runCatching {
            val seconds = intervalEditText.text.toString().toLong()
            TimeUnit.SECONDS.toMillis(seconds)
        }.getOrDefault(DEFAULT_LOCATION_UPDATE_INTERVAL)
    }

    private fun maxWaitTime(): Long {
        return runCatching {
            val seconds = maxWaitTimeEditText.text.toString().toLong()
            TimeUnit.SECONDS.toMillis(seconds)
        }.getOrDefault(DEFAULT_LOCATION_MAX_WAIT_TIME)
    }

    private fun setupStopPeriodicUpdatesButton() {
        stopPeriodicUpdatesButton.setOnClickListener {
            locationDataSource.stopPeriodicUpdates()
        }
    }

    private fun setupDisplayOnMapButton() {
        displayOnMapButton.setOnClickListener {
            val location = locationDataSource.lastLocation
            if (location != null) {
                displayOnMap(location.latitude, location.longitude)
            }
        }
    }

    private fun displayOnMap(latitude: Double, longitude: Double, zoom: Int = 18) {
        val mapIntent = mapIntent(latitude, longitude, zoom)
        startActivity(mapIntent)
    }

    private fun mapIntent(latitude: Double, longitude: Double, zoom: Int): Intent {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?z=$zoom")
        return Intent(Intent.ACTION_VIEW, gmmIntentUri)
            .setPackage("com.google.android.apps.maps")
    }

    private fun setupStartServiceButton() {
        startServiceButton.setOnClickListener {
            startService(Intent(applicationContext, MonitoringService::class.java))
        }
    }

    private fun setupStopServiceButton() {
        stopServiceButton.setOnClickListener {
            stopService(Intent(applicationContext, MonitoringService::class.java))
        }
    }

    private fun setupLocationRendering() {
        val locationObserver = Observer<Location> { displayLocation(it) }
        locationDataSource.getLocationLiveData().observe(this, locationObserver)
    }

    private fun displayLocation(location: Location) {
        latitudeTextView.text = location.latitude.toString()
        longitudeTextView.text = location.longitude.toString()
        updateTimeTextView.text = Dependencies.localTimeFormatter.format(location.time)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val UPDATE_INTERVAL_KEY = "UpdateInterval"
        private const val MAX_WAIT_TIME_KEY = "MaxWaitTime"
    }
}
