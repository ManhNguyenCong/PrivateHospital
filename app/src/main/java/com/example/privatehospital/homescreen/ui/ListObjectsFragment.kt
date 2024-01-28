package com.example.privatehospital.homescreen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentListObjectsBinding
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE
import com.example.privatehospital.util.findNavControllerSafely

class ListObjectsFragment : Fragment() {

    private val args: ListObjectsFragmentArgs by navArgs()

    private lateinit var binding: FragmentListObjectsBinding

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

    /**
     * This function is used to load title and menu in action bar
     */
    private fun loadActionBar() = activity?.let { activity ->
        activity.findViewById<Toolbar>(R.id.toolbar)?.apply {
            title = when (args.objectTitle) {
                DOCTOR_TITLE -> {
                    getString(R.string.doctor)
                }

                HOSPITAL_TITLE -> {
                    getString(R.string.txtTvHospital)
                }

                else -> {
                    getString(R.string.services)
                }
            }

            menu.clear()
            inflateMenu(R.menu.menu_home_fragment)
            menu.removeItem(R.id.iMenuMore)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.iMenuSearch -> {
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
}