package com.example.privatehospital.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentPersonalInfoBinding
import com.example.privatehospital.homescreen.ui.adapter.AppointmentAdapter
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.model.User
import com.example.privatehospital.util.findNavControllerSafely
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PersonalInfoFragment : Fragment() {

    /**
     * This argument is used for binding to fragment_personal_info
     */
    private lateinit var binding: FragmentPersonalInfoBinding

    /**
     * This is a view model which is used to handle data before display data
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    /**
     * This adapter is used by recycler view appointment schedule
     */
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPersonalInfoBinding.bind(
            inflater.inflate(
                R.layout.fragment_personal_info,
                container,
                false
            )
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.auth.currentUser?.let { user ->
            // Create adapter for recycler view Appointment
            adapter = AppointmentAdapter(
                setItemOnClick = { serviceId, _ ->
                    // Navigate to fragment_detail_appointment
                    val action = PersonalInfoFragmentDirections.actionPersonalInfoFragmentToDetailAppointmentFragment(serviceId)
                    findNavControllerSafely()?.navigate(action)
                },
                setServiceName = { tv, serviceId ->
                    viewModel.getServiceById(serviceId).observe(this.viewLifecycleOwner) {
                        tv.text = it.name
                    }
                }
            )
            binding.rvAppointment.adapter = adapter

            // Set event onclick for btnSave to save new infor
            binding.btnSave.setOnClickListener {
                saveNewInfo(user.uid, binding.edtFullName.text.toString())
            }

            // Bind data for this fragment
            viewModel.getUserById(user.uid).observe(this.viewLifecycleOwner) {
                bind(it)
            }
        }
    }

    /**
     * This function is used to bind data for this fragment
     *
     * @param user
     */
    private fun bind(user: User) {
        // Set phone number and full name
        binding.edtPhoneNumber.setText(user.phoneNumber)
        binding.edtFullName.setText(user.fullName)

        // Set list appointment
        viewModel.getAppointmentsByUserId(user.id).observe(this.viewLifecycleOwner) { appointments ->
            if(appointments.isEmpty()) {
                // Check, if empty, show text no appointment
                binding.tvNoAppointment.visibility = View.VISIBLE
                binding.rvAppointment.visibility = View.GONE
            } else {
                // if no empty, show list appointments
                binding.tvNoAppointment.visibility = View.GONE
                binding.rvAppointment.visibility = View.VISIBLE
                adapter.submitList(appointments)
            }
        }
    }

    /**
     * This function is used to save new name when user change info
     *
     * @param fullName
     */
    private fun saveNewInfo(userId: String, fullName: String) {
        if (fullName.isNotEmpty()) {
            viewModel.saveNewInfo(userId, fullName)
        }
    }
}