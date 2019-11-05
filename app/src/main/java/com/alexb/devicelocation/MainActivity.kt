package com.alexb.devicelocation

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

        setupLastLocationButton()
        setupDisplayOnMapButton()
        setupLocationRendering()
    }

    private fun setupLastLocationButton() {
        lastLocationButton.setOnClickListener {
            locationDataSource.activateLastLocationMode()
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
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?z=$zoom")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
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
    }
}
