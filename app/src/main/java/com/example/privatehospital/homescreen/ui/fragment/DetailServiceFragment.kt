package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentDetailServiceBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.findNavControllerSafely
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class DetailServiceFragment : Fragment() {

    /**
     * This is arguments of fragment_detail_service in nav_graph
     */
    private val args: DetailServiceFragmentArgs by navArgs()

    /**
     * This is used for binding to fragment_Detail_service
     */
    private lateinit var binding: FragmentDetailServiceBinding

    /**
     * This is view model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailServiceBinding.bind(
            inflater.inflate(R.layout.fragment_detail_service, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        // Set onclick for btn appointment
        binding.btnAppointment.setOnClickListener {
            if (Firebase.auth.currentUser == null) {
                // Check, if no login, notification need login before set appointment
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.txtSetAppointment))
                    .setMessage(getString(R.string.txtNoteLoginRequirement))
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create().show()
            } else {
                val action =
                    DetailServiceFragmentDirections.actionDetailServiceFragmentToAppointmentFragment(
                        args.id
                    )
                findNavControllerSafely()?.navigate(action)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getServiceById(args.id).observe(this.viewLifecycleOwner) { service ->
            // Set name
            binding.tvName.text = service.name
            // Get and set hospital name
            viewModel.getHospitalById(service.hospitalId).apply {
                observe(this@DetailServiceFragment.viewLifecycleOwner) {
                    binding.tvHospitalName.text = it.name
                    removeObservers(this@DetailServiceFragment.viewLifecycleOwner)
                }
            }
            // Set date-time
            binding.tvDateTime.text = String.format(
                "%02d:00-%02d:00; %s",
                service.timeStart,
                service.timeEnd,
                service.weekDays
            )
            // Set describe
            binding.tvDescribe.text = service.describe
            // Set price
            binding.tvPrice.text = service.cost
        }
    }
}