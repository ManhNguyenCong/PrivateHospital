package com.example.privatehospital.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class HospitalReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: in Receiver")
        intent?.let { intentNN ->
            context?.let { contextNN ->
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Todo handle worker
                    val dataBuilder = Data.Builder()

                    dataBuilder.putString("title", intentNN.getStringExtra("title"))
                    dataBuilder.putString("content", intentNN.getStringExtra("content"))

                    val request = OneTimeWorkRequestBuilder<AppointmentWorker>()
                        .setInputData(dataBuilder.build())
                        .build()

                    WorkManager.getInstance(contextNN).enqueue(request)
                } else {
                    // Todo handle service

                }
            }
        }

    }
}