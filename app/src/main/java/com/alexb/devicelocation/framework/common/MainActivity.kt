package com.alexb.devicelocation.framework.common

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alexb.devicelocation.R
import com.alexb.devicelocation.di.Dependencies
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val locationDataSource by lazy { Dependencies.locationDataSource }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLastLocationButton()
        setupStartPeriodicUpdatesButton()
        setupStopPeriodicUpdatesButton()
        setupDisplayOnMapButton()
        setupStartServiceButton()
        setupStopServiceButton()

        setupLocationRendering()
    }

    private fun setupLastLocationButton() {
        lastLocationButton.setOnClickListener {
            locationDataSource.requestLastLocation()
        }
    }

    private fun setupStartPeriodicUpdatesButton() {
        startPeriodicUpdatesButton.setOnClickListener {
            locationDataSource.startPeriodicUpdates(this)
        }
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
}
