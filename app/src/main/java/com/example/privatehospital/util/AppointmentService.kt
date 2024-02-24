package com.example.privatehospital.util

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

class AppointmentService() : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: in service")

        intent?.let { intentNN ->
            val title = intentNN.getStringExtra("title") ?: "Lịch khám"
            val content = intentNN.getStringExtra("content") ?: "Lịch khám của bạn"

            // Build notification
            val notification = createBaseNotification(this, title, content).build()
            // Show notification
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
            }
        }

        return START_NOT_STICKY
    }
}