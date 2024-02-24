package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Appointment(
    @Json(name = "userId") val userId: String,
    @Json(name = "serviceId")val serviceId: String,
    @Json(name = "dateTime") val dateTime: Long,
    @Json(name = "status") val status: Int
)