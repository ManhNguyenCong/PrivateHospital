package com.example.privatehospital.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentSearchBinding
import com.example.privatehospital.homescreen.ui.adapter.DoctorAdapter
import com.example.privatehospital.homescreen.ui.adapter.HospitalAdapter
import com.example.privatehospital.homescreen.ui.adapter.ServiceAdapter
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE
import com.example.privatehospital.util.findNavControllerSafely

class SearchFragment : Fragment() {

    /**
     * This is arguments of fragment_search in nav_graph
     */
    private val args by navArgs<SearchFragmentArgs>()

    /**
     * This is used for binding to fragment_search
     */
    private lateinit var binding: FragmentSearchBinding

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
        binding = FragmentSearchBinding.bind(
            inflater.inflate(R.layout.fragment_search, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        // Load object search
        when (args.objectTitle) {
            DOCTOR_TITLE -> {
                binding.rBtnSearchOption1.isChecked = true
            }

            HOSPITAL_TITLE -> {
                binding.rBtnSearchOption3.isChecked = true
            }

            else -> {
                binding.rBtnSearchOption2.isChecked = true
            }
        }

        // Set btn search onclick
        binding.btnSearch.setOnClickListener {
            val txtSearch = binding.edtSearchBox.text.toString()

            if (txtSearch.isEmpty()) {
                Toast.makeText(requireContext(), "Hãy nhập thông tin cần tìm...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Visible progress bar
            binding.progressBar.visibility = View.VISIBLE

            if (binding.rBtnSearchOption1.isChecked) {
                // Search doctor
                setupDoctorRecyclerView(txtSearch)
            } else if (binding.rBtnSearchOption2.isChecked) {
                // Search hospital
                setupServicesRecyclerView(txtSearch)
            } else {
                // Search hospital
                setupHospitalRecyclerView(txtSearch)
            }
        }

        return binding.root
    }

    /**
     * This function is used to setup doctor recycler view when object title is "doctor"
     */
    private fun setupDoctorRecyclerView(txtSearch: String) {
        // Init doctor adapter
        val adapter = DoctorAdapter(
            onDoctorClick = { doctorId ->
                val action =
                    SearchFragmentDirections.actionSearchFragmentToDetailDoctorFragment(
                        doctorId
                    )
                findNavControllerSafely()?.navigate(action)
            },
            setMajorName = { tv, majorId ->
                viewModel.getMajorById(majorId).observe(this.viewLifecycleOwner) {
                    tv.text = String.format(getString(R.string.majorInItemDoctor), it.name)
                }
            }
        )
        // Set adapter for recycler view
        binding.rvResultSearch.adapter = adapter
        // Submit lis for recycler view
        viewModel.getTxtDoctorInfo().observe(this.viewLifecycleOwner) { list ->
            adapter.submitList(
                list.filter {
                    it.second.contains(txtSearch)
                }.map {
                    it.first
                }
            )
            // Invisible progress bar
            binding.progressBar.visibility = View.GONE
        }
    }

    /**
     * This function is used to setup services recycler view when object title is name of a category
     */
    private fun setupServicesRecyclerView(txtSearch: String) {
        // Init service adapter
        val adapter = ServiceAdapter(
            setHospitalName = { tv, hospitalId ->
                viewModel.getHospitalById(hospitalId).observe(this.viewLifecycleOwner) {
                    tv.text = it.name
                }
            },
            onServiceClick = { serviceId ->
                val action =
                    ListObjectsFragmentDirections.actionListObjectsFragmentToDetailServiceFragment(
                        serviceId
                    )
                findNavControllerSafely()?.navigate(action)
            }
        )
        // Set adapter for recycler view
        binding.rvResultSearch.adapter = adapter
        // Submit list for recycler view
        viewModel.getTxtServiceInfo().observe(this.viewLifecycleOwner) { list ->
            adapter.submitList(
                list.filter { it.second.contains(txtSearch) }
                    .map { it.first }
            )
            // Invisible progress bar
            binding.progressBar.visibility = View.GONE
        }
    }

    /**
     * This function is used to setup hospital recycler view when object title is "hospital"
     */
    private fun setupHospitalRecyclerView(txtSearch: String) {
        // Init hospital adapter
        val adapter = HospitalAdapter() { hospitalId ->
            val action =
                ListObjectsFragmentDirections.actionListObjectsFragmentToDetailHospitalFragment(
                    hospitalId
                )
            findNavControllerSafely()?.navigate(action)
        }
        // Set adapter for recycler view
        binding.rvResultSearch.adapter = adapter
        // Submit list for recycler view
        viewModel.getTxtHospitalInfo().observe(this.viewLifecycleOwner) { list ->
            adapter.submitList(
                list.filter {
                    it.second.contains(txtSearch)
                }.map {
                    it.first
                }
            )
            // Invisible progress bar
            binding.progressBar.visibility = View.GONE
        }
    }
}