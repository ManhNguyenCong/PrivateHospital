package com.example.privatehospital.homescreen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentAppointmentBinding

class AppointmentFragment : Fragment() {

    private lateinit var binding: FragmentAppointmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.bind(
            inflater.inflate(R.layout.fragment_appointment, container, false)
        )

        setupRadioButton()

        return binding.root
    }

    /**
     * This function is used to setup text color and event onclick for radio button tomorrow and afternoon
     */
    private fun setupRadioButton() {
        // Load text color when open
        loadTextColorOfRadioButton()

        binding.rBtnTomorrow.apply {
            setOnClickListener {
                // Set checked when click
                if (!isChecked) {
                    isChecked = true
                    // If it is checked, other radio button isn't checked
                    binding.rBtnAfternoon.isChecked = false
                }

                // Reload text color
                loadTextColorOfRadioButton()
            }
        }

        binding.rBtnAfternoon.apply {
            // Set checked when click
            setOnClickListener {
                if (!isChecked) {
                    isChecked = true
                    // If it is checked, other radio button isn't checked
                    binding.rBtnTomorrow.isChecked = false
                }

                // Reload text color
                loadTextColorOfRadioButton()
            }
        }
    }

    /**
     * This function is used to load text color for radio buttons
     */
    private fun loadTextColorOfRadioButton() {
        binding.rBtnTomorrow.apply {
            if (isChecked) {
                setTextColor(resources.getColor(R.color.white, null))
                binding.rBtnAfternoon.setTextColor(resources.getColor(R.color.blue116696, null))
            } else {
                setTextColor(resources.getColor(R.color.blue116696, null))
                binding.rBtnAfternoon.setTextColor(resources.getColor(R.color.white, null))
            }
        }

        loadLimitOfNumberPicker()
    }

    private fun loadLimitOfNumberPicker() {
        binding.npMinutes.textSize = resources.getDimension(R.dimen.SmallTextSize)
        binding.npHours.textSize = resources.getDimension(R.dimen.SmallTextSize)
        binding.npMinutes.minValue = 0
        binding.npMinutes.maxValue = 45

        binding.npMinutes.setOnValueChangedListener { _, _, new ->
            if(new % 15 != 0) {
                binding.npMinutes.value = (new / 15 + 1) * 15
            }
        }

        if (binding.rBtnTomorrow.isChecked) {
            binding.npHours.minValue = 8
            binding.npHours.maxValue = 11
        } else {
            binding.npHours.minValue = 13
            binding.npHours.maxValue = 16
        }
    }
}