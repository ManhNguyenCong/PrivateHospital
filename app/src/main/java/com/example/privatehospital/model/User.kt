package com.example.privatehospital.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "id") val id: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "password") val password: String
)
