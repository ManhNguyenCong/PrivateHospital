package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentHomeBinding
import com.example.privatehospital.homescreen.ui.adapter.AppointmentAdapter
import com.example.privatehospital.homescreen.ui.adapter.HospitalAdapter
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.CATEGORY_TITLE
import com.example.privatehospital.util.DOCTOR_TITLE
import com.example.privatehospital.util.HOSPITAL_TITLE
import com.example.privatehospital.util.HospitalReceiver
import com.example.privatehospital.util.SERVICE_TITLE
import com.example.privatehospital.util.TIME_ZONE
import com.example.privatehospital.util.findNavControllerSafely
import com.example.privatehospital.util.format
import com.example.privatehospital.util.toDateTime
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

class HomeFragment : Fragment() {

    /**
     * This is used for binding to fragment_home
     */
    private lateinit var binding: FragmentHomeBinding

    /**
     * This is view model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    /**
     * This is Firebase Auth
     */
    private val auth = Firebase.auth

    override fun onStart() {
        super.onStart()
        // TOdo handle notification nest appointment
        auth.currentUser?.uid?.let { userId ->
            viewModel.getTodayAppointments(userId).observe(this.viewLifecycleOwner) { appointments ->
                // Get alarm manager
                val alarmManager =
                    activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                appointments.firstOrNull { it.status == 1 }?.apply {
                    val intent = Intent(context, HospitalReceiver::class.java)
                    // Put title of notification is name of service
                    viewModel.getServiceById(this.serviceId).let { serviceLiveData ->
                        serviceLiveData.observe(this@HomeFragment.viewLifecycleOwner) { service ->
                            intent.putExtra("title", service.name)
                            serviceLiveData.removeObservers(this@HomeFragment.viewLifecycleOwner)
                        }
                    }
                    // Put content of notification
                    intent.putExtra(
                        "content",
                        String.format(
                            getString(R.string.txtHaveAppointment),
                            this.dateTime.toDateTime().format()
                        )
                    )

                    // Create pending intent
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    // Get time notification (before 30 minutes)
                    val timeZone = TimeZone.of(TIME_ZONE)
                    val timeNotification = this.dateTime.toDateTime().toInstant(timeZone)
                        .plus(30, DateTimeUnit.MINUTE, timeZone)

                    // Set alarm
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        timeNotification.toEpochMilliseconds(),
                        pendingIntent
                    )
                } ?: {
                    if (Build.VERSION.SDK_INT >= 34) {
                        alarmManager.cancelAll()
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.bind(
            inflater.inflate(R.layout.fragment_home, container, false)
        )

        binding.tvShowAllAppointmentSchedule.visibility = View.INVISIBLE

        // Set menu for toolbar
        setUpMenuInToolbar()

        // Set event click to show list doctors
        binding.imgDoctor.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(DOCTOR_TITLE)
            findNavControllerSafely()?.navigate(action)
        }

        // Set event click to show list hospitals
        binding.imgHospital.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(
                HOSPITAL_TITLE
            )
            findNavControllerSafely()?.navigate(action)
        }

        // Set event click to show list categories
        binding.imgService.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToListObjectsFragment(
                CATEGORY_TITLE
            )
            findNavControllerSafely()?.navigate(action)
        }

//        // Set event click to login
//        binding.tvSuggestLogin.setOnClickListener {
//            val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
//            findNavControllerSafely()?.navigate(action)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO Check, if no shortcut isn't used
        if (true) {
            binding.tvShortcutTitle.visibility = View.GONE
            binding.rvShortcuts.visibility = View.GONE
        } else {
            // If shortcut is used
            binding.tvShortcutTitle.visibility = View.VISIBLE
            binding.rvShortcuts.visibility = View.VISIBLE
        }

        // Set adapter for recycler view Top hospital
        HospitalAdapter { hospitalId ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailHospitalFragment(hospitalId)
            findNavControllerSafely()?.navigate(action)
        }.apply {
            binding.rvTopHospitals.adapter = this
            // Submit list for hospital adapter
            viewModel.getHospitals().observe(this@HomeFragment.viewLifecycleOwner) { hospitals ->
                // Todo handle sort hospital
                this.submitList(
                    hospitals.sortedByDescending { hospital ->
                        (hospital.numOfPersonRated - 50) * hospital.rating / hospital.numOfPersonRated
                    }
                )
            }
        }

        // Load fragment with login status
        loadFragmentWithLoginStatus(auth.currentUser)
        viewModel.getFirebaseUser().observe(this.viewLifecycleOwner) {
            loadFragmentWithLoginStatus(it)
        }
    }

    /**
     * This function is used to set up menu for toolbar
     */
    private fun setUpMenuInToolbar() = activity?.let { activity ->
        activity.findViewById<Toolbar>(R.id.toolbar)?.apply {
            // Clear menu
            menu.clear()
            // Inflate menu
            inflateMenu(R.menu.menu_home_fragment)
            // Set onclick for menu item
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.iMenuSearch -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToSearchFragment(objectTitle = SERVICE_TITLE)
                        findNavControllerSafely()?.navigate(action)
                        true
                    }

                    R.id.iMenuMore -> {
                        loadFragmentWithLoginStatus(auth.currentUser)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    /**
     * This function is used to load layout and data with login status: login or no login
     */
    private fun loadFragmentWithLoginStatus(firebaseUser: FirebaseUser?) {
        // Get user id
        val userId = firebaseUser?.uid
        // Check, if login or no login
        if (userId == null) {
            // Set default text greeting
            binding.tvGreeting.text = getString(R.string.welcome_to_private_hospital)
            // Invisible recycler view Appointment schedule
            binding.cvAppointmentSchedule.visibility = View.GONE
            // Visible text view suggest login
            binding.tvSuggestLogin.visibility = View.VISIBLE
        } else {
            binding.cvAppointmentSchedule.visibility = View.VISIBLE
            // Invisible text view suggest login
            binding.tvSuggestLogin.visibility = View.INVISIBLE
            // Get user name and display greeting
            viewModel.getUserById(userId).apply {
                observe(this@HomeFragment.viewLifecycleOwner) { user ->
                    // Set text of text view greeting and if
                    binding.tvGreeting.text = String.format(
                        getString(R.string.txtGreeting),
                        user.fullName.ifEmpty { getString(R.string.user) })
                    removeObservers(this@HomeFragment.viewLifecycleOwner)
                }
            }
            // Set up recycler view Appointment schedule
            binding.rvAppointmentSchedule.apply {
                // Visible it
                visibility = View.VISIBLE
                // Set adapter for recycler view Appointment schedule
                val adapter = AppointmentAdapter(
                    setItemOnClick = { serviceId, _ ->
                        // Set event navigate to fragment_detail_appointment
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToDetailAppointmentFragment(
                                serviceId
                            )
                        findNavControllerSafely()?.navigate(action)
                    },
                    setServiceName = { tv, serviceId ->
                        // Get and set service name
                        viewModel.getServiceById(serviceId).apply {
                            observe(this@HomeFragment.viewLifecycleOwner) { service ->
                                tv.text = service.name
                                removeObservers(this@HomeFragment.viewLifecycleOwner)
                            }
                        }
                    }
                )
                binding.rvAppointmentSchedule.adapter = adapter

                // Submit list for it
                viewModel.getTodayAppointments(userId)
                    .observe(this@HomeFragment.viewLifecycleOwner) {
                        if (it.isEmpty()) {
                            // Check list, if empty, show text no appointment schedule
                            binding.tvNoAppointmentSchedule.visibility = View.VISIBLE
                            binding.rvAppointmentSchedule.visibility = View.GONE
                        } else {
                            // If no empty, show recycler view appointment schedule
                            binding.tvNoAppointmentSchedule.visibility = View.GONE
                            binding.rvAppointmentSchedule.visibility = View.VISIBLE
                            // Submit list for recycler view
                            adapter.submitList(it)
                        }
                    }
            }
        }
    }
}