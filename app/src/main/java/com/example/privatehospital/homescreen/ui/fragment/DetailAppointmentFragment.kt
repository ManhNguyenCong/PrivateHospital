package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentDetailAppointmentBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.model.Appointment
import com.example.privatehospital.model.Service
import com.example.privatehospital.util.DEFAULT_TIME_CAN_REMOVE
import com.example.privatehospital.util.TAG
import com.example.privatehospital.util.TIME_ZONE
import com.example.privatehospital.util.findNavControllerSafely
import com.example.privatehospital.util.format
import com.example.privatehospital.util.toDateTime
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class DetailAppointmentFragment : Fragment() {

    /**
     * This is arguments of fragment_detail_appointment in nav_graph
     */
    private val args: DetailAppointmentFragmentArgs by navArgs()

    /**
     * This argument is used for binding to fragment_detail_appointment
     */
    private lateinit var binding: FragmentDetailAppointmentBinding

    /**
     * This is a view model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailAppointmentBinding.bind(
            inflater.inflate(R.layout.fragment_detail_appointment, container, false)
        )

        // Set onclick for btnRemoveAppointment
        binding.btnRemoveAppointment.setOnClickListener {
            // Show dialog confirm remove
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.txtRemoveAppointmentTitle))
                .setMessage(getString(R.string.txtConfirmRemoveAppointment))
                .setPositiveButton(getString(R.string.txtConfirm)) { _, _ ->
                    // If confirm, remove appointment
                    removeAppointment()
                }
                .setNegativeButton(getString(R.string.txtCancel)) { _, _ -> }
                .create().show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.auth.currentUser?.let { user ->
            // Get service id
            val serviceId = args.serviceId
            // Get and bind service info
            viewModel.getServiceById(serviceId).observe(this.viewLifecycleOwner) {
                bindServiceInfo(it)
            }
            // Get and bind appointment info
            viewModel.getAppointmentByKey(user.uid, serviceId).observe(this.viewLifecycleOwner) {
                bindAppointmentInfo(it)
            }
        }

    }

    /**
     * This function is used to bind service info of this appointment
     */
    private fun bindServiceInfo(service: Service) {
        // Set nam service
        binding.tvName.text = service.name
        // Get and set name of hospital
        viewModel.getHospitalById(service.hospitalId).apply {
            this.observe(this@DetailAppointmentFragment.viewLifecycleOwner) {
                binding.tvHospitalName.text = it.name
                removeObservers(this@DetailAppointmentFragment.viewLifecycleOwner)
            }
        }
        // Set describe
        binding.tvDescribe.text = service.describe
        // Set price
        binding.tvPrice.text = service.cost
    }

    /**
     * This function is used to bind appointment info of this appointment!
     */
    private fun bindAppointmentInfo(appointment: Appointment) {
        appointment.dateTime.toDateTime().apply {
            // Display date time
            binding.tvDateTime.text = this.format()

            // Check, if duration between now and date time set > 12h
            val timeZone = TimeZone.of(TIME_ZONE)
            val now = Clock.System.now()
                .minus(DEFAULT_TIME_CAN_REMOVE, DateTimeUnit.HOUR, timeZone)
                .toLocalDateTime(timeZone)

            binding.btnRemoveAppointment.isEnabled = this > now
        }
        // Set appointment status
        binding.tvStatus.text =
            resources.getStringArray(R.array.appointmentStatus)[appointment.status]
    }

    /**
     * This function is used to remove this appointment
     */
    private fun removeAppointment() = Firebase.auth.currentUser?.apply {
        //Remove appointment
        viewModel.removeAppointmentByKey(this.uid, args.serviceId)

        // Navigate up
        findNavControllerSafely()?.navigateUp()
    }
}