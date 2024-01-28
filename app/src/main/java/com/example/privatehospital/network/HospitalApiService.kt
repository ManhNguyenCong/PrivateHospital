package com.example.privatehospital.network

import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Major
import com.example.privatehospital.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface HospitalApiService {
    @GET("tblMajor.json")
    suspend fun getMajors(): List<Major>

    @GET("tblCategory.json")
    suspend fun getCategories(): List<Category>
}

object HospitalApi {
    val retrofitService: HospitalApiService by lazy {
        retrofit.create(HospitalApiService::class.java)
    }
}