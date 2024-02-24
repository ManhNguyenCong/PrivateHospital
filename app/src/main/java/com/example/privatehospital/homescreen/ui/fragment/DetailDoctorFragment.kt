package com.example.privatehospital.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentDetailDoctorBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.loadImage

class DetailDoctorFragment : Fragment() {

    /**
     * This is arguments of fragment_detail_doctor in nav_graph
     */
    private val args: DetailDoctorFragmentArgs by navArgs()

    /**
     * This is used for binding to fragment_detail_doctor
     */
    private lateinit var binding: FragmentDetailDoctorBinding

    /**
     * This view model is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailDoctorBinding.bind(
            inflater.inflate(R.layout.fragment_detail_doctor, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDoctorById(args.id).observe(this.viewLifecycleOwner) { doctor ->
            // Set image
            binding.imgAvatar.loadImage(doctor.image)
            // Set name
            binding.tvName.text = doctor.name
            // Set phone number
            binding.tvPhoneNumber.text = doctor.numberPhone
            // Set sex
            binding.tvSex.text = doctor.sex
            // Get and set hospital name
            viewModel.getHospitalById(doctor.hospitalId).apply {
                observe(this@DetailDoctorFragment.viewLifecycleOwner) {
                    binding.tvHospital.text = it.name
                    removeObservers(this@DetailDoctorFragment.viewLifecycleOwner)
                }
            }
            // Get and set major name
            viewModel.getMajorById(doctor.majorId).apply {
                observe(this@DetailDoctorFragment.viewLifecycleOwner) {
                    binding.tvMajor.text = it.name
                    removeObservers(this@DetailDoctorFragment.viewLifecycleOwner)
                }
            }
            // Set describe
            binding.tvDescribe.text = doctor.describe
        }
    }
}