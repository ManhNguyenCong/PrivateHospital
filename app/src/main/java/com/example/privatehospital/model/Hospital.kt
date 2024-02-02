package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Hospital(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "rating") val rating: Float,
    @Json(name = "numOfPersonRated") val numOfPersonRated: Int,
    @Json(name = "address") val address: String,
    @Json(name = "images") val images: List<String>,
    @Json(name = "describe") val describe: String,
    @Json(name = "email") val email: String,
    @Json(name = "hotline") val hotline: String,
    @Json(name = "website") val website: String
)