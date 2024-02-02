package com.example.privatehospital.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentHomeBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.CATEGORY_TITLE
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE
import com.example.privatehospital.util.SERVICE_TITLE
import com.example.privatehospital.util.findNavControllerSafely

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.bind(
            inflater.inflate(R.layout.fragment_home, container, false)
        )

        // Set menu for toolbar
        setUpMenuInToolbar()

        binding.imgDoctor.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(DOCTOR_TITLE)
            findNavControllerSafely()?.navigate(action)
        }

        binding.imgHospital.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(
                HOSPITAL_TITLE
            )
            findNavControllerSafely()?.navigate(action)
        }

        binding.imgService.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(
                CATEGORY_TITLE
            )
            findNavControllerSafely()?.navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGreeting.text = getString(R.string.welcome_to_private_hospital)
    }

    /**
     * This function is used to set up menu for toolbar
     */
    private fun setUpMenuInToolbar() = activity?.let { activity ->
        activity.findViewById<Toolbar>(R.id.toolbar)?.apply {

            menu.clear()
            inflateMenu(R.menu.menu_home_fragment)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.iMenuSearch -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToSearchFragment(objectTitle = SERVICE_TITLE)
                        findNavControllerSafely()?.navigate(action)
                        true
                    }
                    R.id.iMenuMore -> {
                        true
                    }
                    else -> false
                }
            }
        }
    }
}