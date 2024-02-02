package com.example.privatehospital.model

import com.squareup.moshi.Json

data class Service(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "timeStart") val timeStart: Int,
    @Json(name = "timeEnd") val timeEnd: Int,
    @Json(name = "stepTime") val stepTime: Int,
    @Json(name = "weekDays") val weekDays: String,
    @Json(name = "cost") val cost: String,
    @Json(name = "describe") val describe: String,
    @Json(name = "hospitalId") val hospitalId: String,
    @Json(name = "categoryId") val categoryId: String
)
