package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Major(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String
)
