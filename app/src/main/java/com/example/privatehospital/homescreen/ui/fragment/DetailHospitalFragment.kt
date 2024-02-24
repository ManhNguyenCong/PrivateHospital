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
import com.example.privatehospital.databinding.FragmentDetailHospitalBinding
import com.example.privatehospital.homescreen.ui.adapter.ImageAdapter
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory

class DetailHospitalFragment : Fragment() {

    /**
     * This is arguments of this fragment in nav_graph
     */
    private val args: DetailHospitalFragmentArgs by navArgs()

    /**
     * This is used for binding to fragment_detail_hospital
     */
    private lateinit var binding: FragmentDetailHospitalBinding

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
        binding = FragmentDetailHospitalBinding.bind(
            inflater.inflate(R.layout.fragment_detail_hospital, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHospitalById(args.id).observe(this.viewLifecycleOwner) { hospital ->
            binding.rvImages.adapter = ImageAdapter(hospital.images)
            binding.tvName.text = hospital.name
            binding.tvDescribe.text = hospital.describe
            binding.tvHotline.text = hospital.hotline
            binding.tvEmail.text = hospital.email
            binding.tvWebsite.text = hospital.website
            binding.tvAddress.text = hospital.address
            binding.rbAppreciate.rating = hospital.rating
            binding.tvNumOfPersonRated.text = String.format("(%d)", hospital.numOfPersonRated)
        }
    }
}