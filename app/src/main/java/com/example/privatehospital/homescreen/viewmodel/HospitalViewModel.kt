package com.example.privatehospital.homescreen.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.privatehospital.R
import com.example.privatehospital.model.Appointment
import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Doctor
import com.example.privatehospital.model.Hospital
import com.example.privatehospital.model.Major
import com.example.privatehospital.model.Service
import com.example.privatehospital.model.User
import com.example.privatehospital.network.HospitalApi
import com.example.privatehospital.util.REGEX_PASSWORD
import com.example.privatehospital.util.TAG
import com.example.privatehospital.util.TIME_ZONE
import com.example.privatehospital.util.md5
import com.example.privatehospital.util.toDateTime
import com.example.privatehospital.util.toEpochMilliseconds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.regex.Pattern

class HospitalViewModel(private val context: Context) : ViewModel() {

    /**
     * This argument is used to check if navigate form fragment_register to fragment_login.
     * If true, don't sign out
     *
     */
    val registerStatus: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * This function is used to get all majors and sort them by name
     */
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

    /**
     * This function is used to get major by id
     *
     * @param id
     */
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

    /**
     * This function is used to get categories by major id
     *
     * @param majorId
     */
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

    /**
     * This function is used to get services as strings
     */
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

    /**
     * This function is used to get services by category id
     *
     * @param categoryId
     */
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

    /**
     * This function is used to get service by id
     *
     * @param id
     */
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

    /**
     * This function is used to get all hospitals and sort descend by numOfPersonRated
     */
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

    /**
     * This function is used to get hospitals as strings
     */
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

    /**
     * This function is used to get hospital by id
     *
     * @param id
     */
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

    /**
     * This function is used to get all doctors and sort by first name
     */
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

    /**
     * This function is used to get doctors as strings
     */
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

    /**
     * This function is used to get doctor by id
     *
     * @param id
     */
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

    /**
     * This function is used to add a new user
     *
     * @param id
     * @param phoneNumber
     * @param password
     */
    fun addNewUser(id: String, phoneNumber: String, password: String) {
        viewModelScope.launch {
            try {
                HospitalApi.retrofitService.createAnUser(
                    id,
                    User(id, "", phoneNumber, password.md5())
                )
            } catch (e: Exception) {
                Log.e(TAG, "addNewUser: " + e.message)
            }
        }
    }

    /**
     * This function is used to get user by phone number and password
     *
     * @param phoneNumber
     * @param password
     */
    fun getUser(phoneNumber: String, password: String): LiveData<User> {
        val users = MutableLiveData<User>()

        viewModelScope.launch {
            try {
                users.value = HospitalApi.retrofitService.getUsers().values.toList()
                    .firstOrNull { user ->
                        Log.d(TAG, "getUser: ${password.md5() == user.password}")
                        user.phoneNumber == phoneNumber && password.md5() == user.password
                    }
            } catch (e: Exception) {
                Log.e(TAG, "getUsers: " + e.message)
            }
        }

        return users
    }

    /**
     * This function is used to get an user by it's id
     *
     * @param id
     */
    fun getUserById(id: String): LiveData<User> {
        val user = MutableLiveData<User>()
        viewModelScope.launch {
            try {
                user.value = HospitalApi.retrofitService.getUserById(id)
            } catch (e: Exception) {
                Log.e(TAG, "getUserById: " + e.message)
            }
        }
        return user
    }

    /**
     * This function is used to get appointments by userId
     *
     * @param userId
     */
    fun getAppointmentsByUserId(userId: String): LiveData<List<Appointment>> {
        val appointments = MutableLiveData<List<Appointment>>()

        viewModelScope.launch {
            try {
                appointments.value =
                    HospitalApi.retrofitService.getAppointmentsByUserId(userId)
                        .values.toList().sortedBy { it.dateTime }
            } catch (e: Exception) {
                Log.e(TAG, "getAppointmentsByUserId: " + e.message)
            }
        }

        return appointments
    }

    /**
     * This function is used to get appointment by key (contains: userId and serviceId)
     *
     * @param userId
     * @param serviceId
     */
    fun getAppointmentByKey(userId: String, serviceId: String): LiveData<Appointment> {
        val appointment = MutableLiveData<Appointment>()

        viewModelScope.launch {
            try {
                appointment.value =
                    HospitalApi.retrofitService.getAppointmentByKey(userId, serviceId)
            } catch (e: Exception) {
                Log.e(TAG, "getAppointmentByKey: " + e.message)
            }
        }

        return appointment
    }

    /**
     * This function is used to add a new appointment
     */
    fun addAnAppointment(userId: String, serviceId: String, dateTime: LocalDateTime) {
        viewModelScope.launch {
            try {
                // Add a new appointment
                HospitalApi.retrofitService.addAnAppointment(
                    userId,
                    serviceId,
                    Appointment(
                        userId,
                        serviceId,
                        dateTime.toEpochMilliseconds(),
                        0
                    )
                )
                // Show message add new appointment successful
                Toast.makeText(
                    context,
                    context.getString(R.string.txtSetAppointmentSuccessful), Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "addAnAppointment: " + e.message)
                // Show message add new appointment failure
                Toast.makeText(
                    context,
                    context.getString(R.string.txtSetAppointmentFailure), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * This function is used to remove appointment by key (contains: user id and service id
     *
     * @param userId
     * @param serviceId
     */
    fun removeAppointmentByKey(userId: String, serviceId: String) {
        viewModelScope.launch {
            try {
                HospitalApi.retrofitService.removeAppointmentByKey(userId, serviceId)
            } catch (e: Exception) {
                Log.e(TAG, "removeAppointmentByKey: " + e.message)
            }
        }
    }

    /**
     * This function is used to validate entry of phone number
     *
     * @param phoneNumber
     */
    fun validEntryPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.isNotEmpty()
    }

    /**
     * This function is used to validate password (least 8 letters with least 1 uppercase letter,
     * least 1 digit.
     *
     * @param password
     */
    fun validEntryPassword(password: String): Boolean {
        return Pattern.compile(REGEX_PASSWORD).matcher(password).matches()
    }

    /**
     * This function is used to validate re-password
     *
     * @param password
     * @param rePassword
     */
    fun validRePassword(password: String, rePassword: String): Boolean {
        return password == rePassword
    }

    /**
     * This function is used to get firebase user
     */
    fun getFirebaseUser(): LiveData<FirebaseUser> {
        val user = MutableLiveData<FirebaseUser>()

        try {
            Firebase.auth.addAuthStateListener { auth ->
                user.value = auth.currentUser
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFirebaseAuth: " + e.message)
        }

        return user
    }

    /**
     * This function is used to save new info of user by id
     *
     * @param id
     * @param fullName
     */
    fun saveNewInfo(id: String, fullName: String) {
        viewModelScope.launch {
            try {
                HospitalApi.retrofitService.saveNewName(id, fullName)
                Toast.makeText(
                    context,
                    context.getString(R.string.txtSaveSuccessful), Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveNewInfo: " + e.message)
                Toast.makeText(
                    context,
                    context.getString(R.string.txtSaveFailure), Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    /**
     * This function is used to get all appointments in today
     *
     * @param userId
     */
    fun getTodayAppointments(userId: String): LiveData<List<Appointment>> {
        return getAppointmentsByUserId(userId).map {
            it.filter { appointment ->
                val now = Clock.System.now().toLocalDateTime(TimeZone.of(TIME_ZONE))
                appointment.dateTime.toDateTime().let { dateTime ->
                    dateTime.date == now.date
                }
            }.sortedBy { appointment -> appointment.dateTime }
        }
    }

}

class HospitalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HospitalViewModel(context) as T
        }

        return super.create(modelClass)
    }
}