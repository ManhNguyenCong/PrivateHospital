package com.example.privatehospital.network

import com.example.privatehospital.model.Appointment
import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Doctor
import com.example.privatehospital.model.Hospital
import com.example.privatehospital.model.Major
import com.example.privatehospital.model.Service
import com.example.privatehospital.model.User
import com.example.privatehospital.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("tblService.json")
    suspend fun getServices(): List<Service>

    @GET("tblHospital.json")
    suspend fun getHospitals(): List<Hospital>

    @GET("tblDoctor.json")
    suspend fun getDoctors(): List<Doctor>

    @PUT("tblUser/{id}.json")
    suspend fun createAnUser(@Path("id") id: String, @Body user: User)

    @GET("tblUser/{id}.json")
    suspend fun getUserById(@Path("id") id: String): User

    @GET("tblUser.json")
    suspend fun getUsers(): Map<String, User>

    @PUT("tblUser/{id}/fullName.json")
    suspend fun saveNewName(@Path("id") id: String, @Body fullName: String)

    @GET("tblAppointment/{userId}.json")
    suspend fun getAppointmentsByUserId(@Path("userId") userId: String): Map<String, Appointment>

    @GET("tblAppointment/{userId}/{serviceId}.json")
    suspend fun getAppointmentByKey(
        @Path("userId") userId: String,
        @Path("serviceId") serviceId: String
    ): Appointment

    @PUT("tblAppointment/{userId}/{serviceId}.json")
    suspend fun addAnAppointment(
        @Path("userId") userId: String,
        @Path("serviceId") serviceId: String,
        @Body appointment: Appointment
    )

    @DELETE("tblAppointment/{userId}/{serviceId}.json")
    suspend fun removeAppointmentByKey(
        @Path("userId") userId: String,
        @Path("serviceId") serviceId: String
    )
}

object HospitalApi {
    val retrofitService: HospitalApiService by lazy {
        retrofit.create(HospitalApiService::class.java)
    }
}