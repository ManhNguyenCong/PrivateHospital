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
import com.example.privatehospital.databinding.FragmentListObjectsBinding
import com.example.privatehospital.homescreen.ui.adapter.DoctorAdapter
import com.example.privatehospital.homescreen.ui.adapter.HospitalAdapter
import com.example.privatehospital.homescreen.ui.adapter.MajorAdapter
import com.example.privatehospital.homescreen.ui.adapter.ServiceAdapter
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.CATEGORY_TITLE
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE
import com.example.privatehospital.util.findNavControllerSafely

class ListObjectsFragment : Fragment() {

    /**
     * This is arguments of this fragment in nav_graph
     */
    private val args: ListObjectsFragmentArgs by navArgs()

    /**
     * This is used to bind to fragment_list_objects
     */
    private lateinit var binding: FragmentListObjectsBinding

    /**
     * This is used to handle data before it is displayed
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListObjectsBinding.bind(
            inflater.inflate(R.layout.fragment_list_objects, container, false)
        )

        // Load action bar
        loadActionBar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load adapter
        loadRecyclerView()
    }

    /**
     * This function is used to load title and menu in action bar
     */
    private fun loadActionBar() = activity?.let { activity ->
        activity.findViewById<Toolbar>(R.id.toolbar)?.apply {
            // Load title
            title = when (args.objectTitle) {
                DOCTOR_TITLE -> {
                    getString(R.string.doctor)
                }

                HOSPITAL_TITLE -> {
                    getString(R.string.txtTvHospital)
                }

                CATEGORY_TITLE -> {
                    getString(R.string.services)
                }

                else -> {
                    args.objectTitle
                }
            }

            // Clear menu
            menu.clear()
            // Inflate menu
            inflateMenu(R.menu.menu_home_fragment)
            // Remove item menu which is don't used
            menu.removeItem(R.id.iMenuMore)
            // Set item menu on click
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.iMenuSearch -> {
                        // Navigate to Search fragment
                        val action =
                            ListObjectsFragmentDirections.actionListObjectsFragmentToSearchFragment(
                                args.objectTitle
                            )
                        findNavControllerSafely()?.navigate(action)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    /**
     * This function is used to load recycler view with each object title
     */
    private fun loadRecyclerView() {
        when (args.objectTitle) {
            CATEGORY_TITLE -> {
                setupMajorRecyclerView()
            }

            DOCTOR_TITLE -> {
                setupDoctorRecyclerView()
            }

            HOSPITAL_TITLE -> {
                setupHospitalRecyclerView()
            }

            else -> {
                setupServicesRecyclerView()
            }
        }
    }

    /**
     * This function is used to setup doctor recycler view when object title is "doctor"
     */
    private fun setupDoctorRecyclerView() {
        // Init doctor adapter
        val adapter = DoctorAdapter(
            onDoctorClick = { doctorId ->
                val action =
                    ListObjectsFragmentDirections.actionListObjectsFragmentToDetailDoctorFragment(
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
        binding.rvObjects.adapter = adapter
        // Submit lis for recycler view
        viewModel.getDoctors().observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    /**
     * This function is used to set up major recycler view when object title is "category"
     */
    private fun setupMajorRecyclerView() {
        // Init Major adapter
        val adapter = MajorAdapter(
            submitListForCategoryAdapter = { majorId, categoryAdapter ->
                viewModel.getCategoriesByMajorId(majorId).observe(this.viewLifecycleOwner) {
                    categoryAdapter.submitList(it)
                }
            },
            navToServiceList = { category ->
                val action = ListObjectsFragmentDirections.actionListObjectsFragmentSelf(
                    objectTitle = category.name,
                    categoryId = category.id
                )
                findNavControllerSafely()?.navigate(action)
            }
        )
        // Set adapter for recycler view
        binding.rvObjects.adapter = adapter
        // Submit list for adapter
        viewModel.getMajors().observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    /**
     * This function is used to setup services recycler view when object title is name of a category
     */
    private fun setupServicesRecyclerView() {
        // Get category id
        val categoryId = args.categoryId
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
        binding.rvObjects.adapter = adapter
        // Submit list for recycler view
        viewModel.getServiceByCategoryId(categoryId).observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    /**
     * This function is used to setup hospital recycler view when object title is "hospital"
     */
    private fun setupHospitalRecyclerView() {
        // Init hospital adapter
        val adapter = HospitalAdapter() { hospitalId ->
            val action =
                ListObjectsFragmentDirections.actionListObjectsFragmentToDetailHospitalFragment(
                    hospitalId
                )
            findNavControllerSafely()?.navigate(action)
        }
        // Set adapter for recycler view
        binding.rvObjects.adapter = adapter
        // Submit list for recycler view
        viewModel.getHospitals().observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}