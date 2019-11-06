package com.alexb.devicelocation.components.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alexb.devicelocation.di.Dependencies
import com.alexb.devicelocation.framework.location.LocationSupervisor

class SettingsResolutionActivity : AppCompatActivity() {

    private val locationSupervisor: LocationSupervisor = Dependencies.locationSupervisor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSupervisor.startSettingsResolution(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationSupervisor.REQUEST_CHECK_SETTINGS) {
            dispatchResult(resultCode)
            finish()
        }
    }

    private fun dispatchResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            locationSupervisor.onSettingsResolved()
        } else {
            locationSupervisor.onSettingsResolutionFailed()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsResolutionActivity::class.java)
            context.startActivity(intent)
        }
    }
}
