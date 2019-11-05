package com.alexb.devicelocation

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alexb.devicelocation.di.Dependencies
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val locationDataSource by lazy { Dependencies.locationDataSource }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dependencies.appContext = applicationContext
        instance = this

        setupLastLocationButton()
        setupStartPeriodicUpdatesButton()
        setupStopPeriodicUpdatesButton()
        setupDisplayOnMapButton()
        setupLocationRendering()
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    private fun setupLastLocationButton() {
        lastLocationButton.setOnClickListener {
            locationDataSource.requestLastLocation()
        }
    }

    private fun setupStartPeriodicUpdatesButton() {
        startPeriodicUpdatesButton.setOnClickListener {
            locationDataSource.startPeriodicUpdates()
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

        var instance: Activity? = null
    }
}
