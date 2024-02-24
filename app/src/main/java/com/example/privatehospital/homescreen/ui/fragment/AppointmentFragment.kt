package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentAppointmentBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.model.Service
import com.example.privatehospital.util.TIME_ZONE
import com.example.privatehospital.util.format
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class AppointmentFragment : Fragment() {

    /**
     * This is arguments of fragment_appointment in nav_graph
     */
    private val args: AppointmentFragmentArgs by navArgs()

    /**
     * This is used for binding to fragment_appointment
     */
    private lateinit var binding: FragmentAppointmentBinding

    /**
     * This is a view model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    /**
     * This is
     */
    private var service: Service? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.bind(
            inflater.inflate(R.layout.fragment_appointment, container, false)
        )

        // Set text size of number picker
        binding.npMinutes.textSize = resources.getDimension(R.dimen.VerySmallTextSize)
        binding.npHours.textSize = resources.getDimension(R.dimen.VerySmallTextSize)

        // Set min date for date picker
        binding.dpChooseDate.minDate = Clock.System.now().plus(
            1,
            DateTimeUnit.DAY,
            TimeZone.of(TIME_ZONE)
        ).toEpochMilliseconds()

        // Set up radio button
        setupRadioButton()

        // Set menu for this fragment
        activity?.findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            toolbar.menu.clear()
            toolbar.menu
                .add(getString(R.string.save))
                .setOnMenuItemClickListener {
                    // Save a new appointment
                    addANewAppointment()
                    true
                }.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get service by service id
        viewModel.getServiceById(args.serviceId).observe(this.viewLifecycleOwner) {
            service = it
            loadLimitOfNumberPicker(it.timeStart, it.timeEnd, it.stepTime)
        }
    }

    /**
     * This function is used to setup text color and event onclick for radio button tomorrow and afternoon
     */
    private fun setupRadioButton() {
        // Load text color when open
        onRadioButtonOptionChange()

        binding.rBtnMorning.apply {
            setOnClickListener {
                // Set checked when click
                if (!isChecked) {
                    isChecked = true
                    // If it is checked, other radio button isn't checked
                    binding.rBtnAfternoon.isChecked = false
                }

                // Reload text color
                onRadioButtonOptionChange()
            }
        }

        binding.rBtnAfternoon.apply {
            // Set checked when click
            setOnClickListener {
                if (!isChecked) {
                    isChecked = true
                    // If it is checked, other radio button isn't checked
                    binding.rBtnMorning.isChecked = false
                }

                // Reload text color
                onRadioButtonOptionChange()
            }
        }
    }

    /**
     * This function is used to load text color for radio buttons and load number picker
     * when change option of radio button
     */
    private fun onRadioButtonOptionChange() {
        binding.rBtnMorning.apply {
            if (isChecked) {
                setTextColor(resources.getColor(R.color.white, null))
                binding.rBtnAfternoon.setTextColor(resources.getColor(R.color.blue116696, null))
            } else {
                setTextColor(resources.getColor(R.color.blue116696, null))
                binding.rBtnAfternoon.setTextColor(resources.getColor(R.color.white, null))
            }

            service?.let {
                loadLimitOfNumberPicker(
                    startHour = it.timeStart,
                    endHour = it.timeEnd,
                    stepTime = it.stepTime
                )
            }
        }
    }

    /**
     * This function is used to limit of number picker to choose time
     *
     * @param startHour
     * @param endHour
     * @param stepTime
     */
    private fun loadLimitOfNumberPicker(
        startHour: Int,
        endHour: Int,
        stepTime: Int
    ) {
        binding.npMinutes.minValue = 0
        binding.npMinutes.maxValue = (59 / stepTime * stepTime)

        binding.npMinutes.setOnValueChangedListener { _, _, new ->
            if (new % stepTime != 0) {
                binding.npMinutes.value = (new / stepTime + 1) * stepTime
            }
        }

        // If start hour under 17h, don't enable select morning or afternoon
        if (startHour > 17) {
            binding.rBtnMorning.isEnabled = false
            binding.rBtnAfternoon.isEnabled = false
            binding.npHours.minValue = startHour
            binding.npHours.maxValue = endHour - 1
        } else {
            binding.rBtnMorning.isEnabled = true
            binding.rBtnAfternoon.isEnabled = true

            if (binding.rBtnMorning.isChecked) {
                binding.npHours.minValue = startHour
                binding.npHours.maxValue = 11
            } else {
                binding.npHours.minValue = 13
                binding.npHours.maxValue = endHour - 1
            }
        }
    }

    /**
     * This function is used to add a new appointment
     */
    private fun addANewAppointment() = Firebase.auth.currentUser?.let { user ->
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.txtAddAppointmentTitle))
            .setMessage(
                String.format(
                    getString(R.string.txtAddAppointmentContent),
                    getDateTime().format()
                )
            )
            .setPositiveButton(getString(R.string.txtConfirm)) { _, _ ->
                viewModel.addAnAppointment(
                    userId = user.uid,
                    serviceId = args.serviceId,
                    dateTime = getDateTime()
                )
            }.setNegativeButton(getString(R.string.txtCancel)) { _, _ -> }
            .create().show()
    }

    /**
     * This function is used to get date time which user choose
     */
    private fun getDateTime(): LocalDateTime {
        return LocalDateTime(
            binding.dpChooseDate.year,
            binding.dpChooseDate.month + 1,
            binding.dpChooseDate.dayOfMonth,
            binding.npHours.value,
            binding.npMinutes.value,
        )
    }
}