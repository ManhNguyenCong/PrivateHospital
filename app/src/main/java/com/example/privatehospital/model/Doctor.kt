package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Doctor(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "sex") val sex: String,
    @Json(name = "numberPhone") val numberPhone: String,
    @Json(name = "image") val image: String,
    @Json(name = "describe") val describe: String,
    @Json(name = "majorId") val majorId: String,
    @Json(name = "hospitalId") val hospitalId: String
)