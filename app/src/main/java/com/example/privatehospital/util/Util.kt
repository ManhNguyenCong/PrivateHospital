package com.example.privatehospital.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.privatehospital.R
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.security.MessageDigest

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

/**
 * This function is used to load image with coli library
 *
 * @param imageUrl
 */
fun ImageView.loadImage(imageUrl: String) {
    // Convert url string to uri object
    val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
    // Load image
    this.load(imageUri) {
        placeholder(R.drawable.ic_image_loadding)
        error(R.drawable.ic_image_broken)
    }
}

/**
 * This function is used to
 */
@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}

/**
 * This function is used to convert form milliseconds to local date time
 */
fun Long.toDateTime(): LocalDateTime {
    val tz = TimeZone.of(TIME_ZONE)
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(tz)
}

/**
 * This function is used to convert from local date time to milliseconds
 */
fun LocalDateTime.toEpochMilliseconds(): Long {
    val tz = TimeZone.of(TIME_ZONE)
    return this.toInstant(tz).toEpochMilliseconds()
}

/**
 * This function is used to format datetime as "hh:mm, dd/mm/y"
 */
fun LocalDateTime.format(): String {
    return this.time.format() + ", " + this.date.format()
}

/**
 * This function is used to format date as "dd/mm/y"
 */
fun LocalDate.format(): String {
    return String.format(
        "%02d/%02d/%d", this.dayOfMonth, this.month.number, this.year
    )
}

/**
 * This function is used to format time as "hh:mm"
 */
fun LocalTime.format(): String {
    return String.format(
        "%02d:%02d", this.hour, this.minute
    )
}

/**
 * This function is used to set text color of text view by color id
 */
fun TextView.setTextColorByColorId(colorId: Int) {
    this.setTextColor(resources.getColor(colorId, null))
}

/**
 * This function is used to create a notification
 *
 * @param context
 * @param title
 * @param content
 */
fun createBaseNotification(context: Context, title: String, content: String): NotificationCompat.Builder {
    return NotificationCompat
        .Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_app_foreground)
        .setContentTitle(title)
        .setContentTitle(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSilent(true)
}

/**
 * This function is a base dialog fragment
 *
 * @param layoutId layout id of dialog fragment
 * @param title title of dialog fragment, set it is empty if don't want set title
 * @param bind func is used to bind data for dialog
 * @param onPositionButtonClick func is performed when click position button
 * @param onNegativeButtonClick func is performed when click negative button
 */
class MyBaseDialogFragment(
    private val layoutId: Int,
    private val title: String,
    private val bind: (View) -> Unit,
    private val onPositionButtonClick: (View) -> Unit,
    private val onNegativeButtonClick: (View) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Create dialog builder
            val builder = AlertDialog.Builder(context)
            // Inflate layout for dialog
            val dialogLayout =
                layoutInflater.inflate(layoutId, null)

            // Binding data
            bind(dialogLayout)

            // Set layout for dialog
            builder.setView(dialogLayout)
                .setPositiveButton("Oke") { _, _ ->
                    // Set event positive button is clicked
                    onPositionButtonClick(dialogLayout)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    onNegativeButtonClick(dialogLayout)
                }

            // If title is not empty, set title else don't set title
            if (title.isNotEmpty()) {
                builder.setTitle(title)
            }

            // Create dialog
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}