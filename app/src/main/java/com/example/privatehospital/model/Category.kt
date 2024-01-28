package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Category (
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: String,
    @Json(name = "majorId") val majorId: String
)