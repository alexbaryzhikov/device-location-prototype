package com.alexb.devicelocation

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLocationClient()

        displayOnMapButton.setOnClickListener {
            displayOnMap(latitude, longitude)
        }
    }

    private fun setupLocationClient() {
        runCatching {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener(::updateLocation)
        }.onFailure {
            Log.e(TAG, "Failed to setup location client", it)
        }
    }

    private fun updateLocation(location: Location) {
        Log.d(TAG, "Location: lat = ${location.latitude}, lon = ${location.longitude}")
        latitude = location.latitude
        longitude = location.longitude
        latitudeTextView.text = location.latitude.toString()
        longitudeTextView.text = location.longitude.toString()
        lastUpdateTextView.text = getTimeFormatter().format(location.time)
    }

    private fun getTimeFormatter(): SimpleDateFormat {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }
    }

    private fun displayOnMap(latitude: Double, longitude: Double, zoom: Int = 18) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?z=$zoom")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
