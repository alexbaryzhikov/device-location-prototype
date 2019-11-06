package com.alexb.devicelocation.framework.common

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.alexb.devicelocation.R

class MonitoringService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Monitoring service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = notificationChannel()
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChannel(): NotificationChannel {
        return NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { setSound(null, null) }
    }

    private fun notification(): Notification? {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.monitoring_service))
            .setSmallIcon(R.drawable.ic_location_on_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val TAG = "MonitoringService"
        private const val CHANNEL_ID = "DeviceLocationChannel"
        private const val CHANNEL_NAME = "Device location"
        private const val NOTIFICATION_ID = 200
    }
}
