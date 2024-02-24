package com.example.privatehospital.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class AppointmentWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Get application context
        val appContext = applicationContext

        // TODO Handle get data and show notification
        val title = inputData.getString("title") ?: "Lịch khám"
        val content = inputData.getString("content") ?: "Lịch khám của bạn"

        // Build notification
        val notification = createBaseNotification(appContext, title, content)
            .build()

        // Show notification
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(appContext).notify(NOTIFICATION_ID, notification)
        }

        return Result.success()
    }
}