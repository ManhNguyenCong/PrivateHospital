package com.example.privatehospital.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Doctor
import com.example.privatehospital.model.Hospital
import com.example.privatehospital.model.Major
import com.example.privatehospital.model.Service
import com.example.privatehospital.network.HospitalApi
import com.example.privatehospital.util.TAG
import kotlinx.coroutines.launch

class HospitalViewModel : ViewModel() {
    fun getMajors(): LiveData<List<Major>> {
        val majors = MutableLiveData<List<Major>>()
        viewModelScope.launch {
            try {
                majors.value = HospitalApi.retrofitService.getMajors().sortedBy { major ->
                    major.name
                }
            } catch (e: Exception) {
                Log.e(TAG, "getMajors: " + e.message)
            }
        }
        return majors
    }

    fun getMajorById(id: String): LiveData<Major> {
        val major = MutableLiveData<Major>()

        viewModelScope.launch {
            try {
                major.value = HospitalApi.retrofitService.getMajors().firstOrNull { major ->
                    major.id == id
                }
            } catch (e: Exception) {
                Log.e(TAG, "getMajorById: " + e.message)
            }
        }

        return major
    }

    fun getCategoriesByMajorId(majorId: String): LiveData<List<Category>> {
        val categories = MutableLiveData<List<Category>>()

        viewModelScope.launch {
            try {
                categories.value = HospitalApi.retrofitService.getCategories()
                    .filter { category -> category.majorId == majorId }
            } catch (e: Exception) {
                Log.e(TAG, "getCategories: " + e.message)
            }
        }

        return categories
    }

    fun getTxtServiceInfo(): LiveData<List<Pair<Service, String>>> {
        val services = MutableLiveData<List<Pair<Service, String>>>()

        viewModelScope.launch {
            try {
                services.value = HospitalApi.retrofitService.getServices()
                    .sortedBy { it.name }
                    .map { service ->
                        // Create txt service info
                        val txtInfo = service.name + "$" +
                                HospitalApi.retrofitService.getHospitals().firstOrNull {
                                    it.id == service.hospitalId
                                }?.name
                        // Return (service, txtServiceInfo)
                        Pair(service, txtInfo)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getTxtServiceInfo: " + e.message)
            }
        }

        return services
    }
    fun getServiceByCategoryId(categoryId: String): LiveData<List<Service>> {
        val services = MutableLiveData<List<Service>>()

        viewModelScope.launch {
            try {
                services.value = HospitalApi.retrofitService.getServices().filter { service ->
                    service.categoryId == categoryId
                }
            } catch (e: Exception) {
                Log.e(TAG, "getServiceByCategoryId: " + e.message)
            }
        }

        return services
    }

    fun getServiceById(id: String): LiveData<Service> {
        val services = MutableLiveData<Service>()

        viewModelScope.launch {
            try {
                services.value = HospitalApi.retrofitService.getServices().firstOrNull { service ->
                    service.id == id
                }
            } catch (e: Exception) {
                Log.e(TAG, "getServiceById: " + e.message)
            }
        }

        return services
    }

    fun getHospitals(): LiveData<List<Hospital>> {
        val hospitals = MutableLiveData<List<Hospital>>()

        viewModelScope.launch {
            try {
                hospitals.value =
                    HospitalApi.retrofitService.getHospitals().sortedByDescending { hospital ->
                        hospital.numOfPersonRated
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getHospitals: " + e.message)
            }
        }

        return hospitals
    }

    fun getTxtHospitalInfo(): LiveData<List<Pair<Hospital, String>>> {
        val hospitals = MutableLiveData<List<Pair<Hospital, String>>>()

        viewModelScope.launch {
            try {
                hospitals.value =
                    HospitalApi.retrofitService.getHospitals().sortedByDescending { hospital ->
                        hospital.numOfPersonRated
                    }.map { hospital ->
                        // Create txt hospital info
                        val txtInfo = hospital.name + "$" + hospital.address + "$" +
                                hospital.email + "$" + hospital.hotline + "$" +
                                hospital.website + "$"
                        // return (hospital, txtHospitalInfo)
                        Pair(hospital, txtInfo)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getTxtHospitalInfo: " + e.message)
            }
        }

        return hospitals
    }

    fun getHospitalById(id: String): LiveData<Hospital> {
        val hospital = MutableLiveData<Hospital>()

        viewModelScope.launch {
            try {
                hospital.value =
                    HospitalApi.retrofitService.getHospitals().firstOrNull { hospital ->
                        hospital.id == id
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getHospitalById: " + e.message)
            }
        }

        return hospital
    }

    fun getDoctors(): LiveData<List<Doctor>> {
        val doctors = MutableLiveData<List<Doctor>>()

        viewModelScope.launch {
            try {
                doctors.value = HospitalApi.retrofitService.getDoctors().sortedBy { doctor ->
                    val fullName = doctor.name.trim()
                    fullName.substring(fullName.lastIndexOf(" "))
                }
            } catch (e: Exception) {
                Log.e(TAG, "getDoctors: " + e.message)
            }
        }

        return doctors
    }

    fun getTxtDoctorInfo(): LiveData<List<Pair<Doctor, String>>> {
        val doctors = MutableLiveData<List<Pair<Doctor, String>>>()

        viewModelScope.launch {
            try {
                doctors.value = HospitalApi.retrofitService.getDoctors().sortedBy { doctor ->
                    val fullName = doctor.name.trim()
                    fullName.substring(fullName.lastIndexOf(" "))
                }.map { doctor ->
                    // Convert doctor info to string
                    var txtInfo =
                        doctor.name + "$" + doctor.sex + "$" + doctor.numberPhone + "$"
                    // Add name hospital
                    txtInfo += HospitalApi.retrofitService.getHospitals()
                        .firstOrNull { hospital -> hospital.id == doctor.hospitalId }
                        ?.name
                    // Add name major
                    txtInfo += "$"
                    txtInfo += HospitalApi.retrofitService.getMajors()
                        .firstOrNull { major -> major.id == doctor.majorId }
                        ?.name
                    // Return (doctor, txtInfoDoctor)
                    Pair(
                        doctor,
                        txtInfo
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "getDoctorsSearched: " + e.message)
            }
        }

        return doctors
    }

    fun getDoctorById(id: String): LiveData<Doctor> {
        val doctors = MutableLiveData<Doctor>()

        viewModelScope.launch {
            try {
                doctors.value = HospitalApi.retrofitService.getDoctors().firstOrNull { it.id == id }
            } catch (e: Exception) {
                Log.e(TAG, "getDoctorById: " + e.message)
            }
        }

        return doctors
    }
}

class HospitalViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HospitalViewModel() as T
        }

        return super.create(modelClass)
    }
}