package com.example.privatehospital.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

fun Fragment.findNavControllerSafely(): NavController? {
    return try {
        findNavController()
    } catch (e: Exception) {
        Toast.makeText(
            requireContext(),
            "Hiện tại đang có lỗi xảy ra...",
            Toast.LENGTH_SHORT
        ).show()
        null
    }
}