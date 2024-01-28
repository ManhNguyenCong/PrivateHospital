package com.example.privatehospital.homescreen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentSearchBinding
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE

class SearchFragment : Fragment() {

    private val args by navArgs<SearchFragmentArgs>()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.bind(
            inflater.inflate(R.layout.fragment_search, container, false)
        )

        // Setup menu in toolbar
        setUpMenuInToolbar()

        // TODO Load object search
        when(args.objectTitle) {
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

        return binding.root
    }

    /**
     * This function is used to setup menu in toolbar
     */
    private fun setUpMenuInToolbar() = activity?.let { activity ->
        activity.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()
    }

}