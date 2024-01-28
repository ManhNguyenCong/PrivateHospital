package com.example.privatehospital.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.privatehospital.model.Category
import com.example.privatehospital.model.Major
import com.example.privatehospital.network.HospitalApi
import com.example.privatehospital.util.TAG
import kotlinx.coroutines.launch

class HospitalViewModel : ViewModel() {
    fun getMajors(): LiveData<List<Major>> {
        val majors = MutableLiveData<List<Major>>()
        viewModelScope.launch {
            try {
                majors.value = HospitalApi.retrofitService.getMajors()
            } catch (e: Exception) {
                Log.e(TAG, "getMajors: " + e.message)
            }
        }
        return majors
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