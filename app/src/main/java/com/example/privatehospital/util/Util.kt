package com.example.privatehospital.util

import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.privatehospital.R

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

fun ImageView.loadImage(imageUrl: String) {
    // Convert url string to uri object
    val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
    // Load image
    this.load(imageUri) {
        placeholder(R.drawable.ic_image_loadding)
        error(R.drawable.ic_image_broken)
    }
}