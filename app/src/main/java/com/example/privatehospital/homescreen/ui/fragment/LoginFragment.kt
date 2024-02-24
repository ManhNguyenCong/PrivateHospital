package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentLoginBinding
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.ACCOUNT
import com.example.privatehospital.util.COUNTRY_PHONE_NUMBER
import com.example.privatehospital.util.MyBaseDialogFragment
import com.example.privatehospital.util.PASSWORD
import com.example.privatehospital.util.PHONE_NUMBER
import com.example.privatehospital.util.TAG
import com.example.privatehospital.util.findNavControllerSafely
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    /**
     * This is arguments of fragment_login in nav_graph
     */
    private val args: LoginFragmentArgs by navArgs()

    /**
     * This is used for binding to fragment_login
     */
    private lateinit var binding: FragmentLoginBinding

    /**
     * This is a view model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    /**
     * This is FirebaseAuth
     */
    private val auth = Firebase.auth

    /**
     * This argument is used to store resendToken
     */
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    /**
     * This is callback object which is called when verify phone number
     */
    private val callbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // TODO if verification failed
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.txtLoginError))
//                        .setMessage(getString(R.string.txtAccountErrorMessage))
                .setMessage("Has error: ${e.message}")
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
                .show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            resendToken = token
            showFragmentConfirmOTPCode(verificationId)
        }
    }

    companion object {
        /**
         * This data store is used to store phone number and password when check box remember is checked
         */
        val Context.dataStoreAccount: DataStore<Preferences> by preferencesDataStore(name = ACCOUNT)

        /**
         * Get key of phone number preference
         */
        val phoneNumberPreference = stringPreferencesKey(PHONE_NUMBER)

        /**
         * Get key of password preference
         */
        val passwordPreference = stringPreferencesKey(PASSWORD)
    }

    override fun onStart() {
        super.onStart()
        // Check if had login, sign out
        viewModel.registerStatus.observe(this.viewLifecycleOwner) {
            if (Firebase.auth.currentUser != null && !it) {
                Firebase.auth.signOut()
            } else if(it) {
                findNavControllerSafely()?.navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.bind(
            inflater.inflate(R.layout.fragment_login, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        // Handle event click btnLogin
        binding.btnLogin.setOnClickListener {
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            val password = binding.edtPassword.text.toString()

            // Check remember options
            if (binding.cbxRemember.isChecked) {
                // Remember account
                rememberAccount(phoneNumber, password)
            } else {
                // Remove account is remembered
                rememberAccount("", "")
            }

            // Check account
            viewModel.getUser(phoneNumber, password).observe(this.viewLifecycleOwner) { user ->
                if (user != null) {
                    signInWithPhoneNumber(phoneNumber)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.txtLoginError))
                        .setMessage(getString(R.string.txtLoginErrorMessage))
                        .setNegativeButton("Cancel") { _, _ -> }
                        .create()
                        .show()
                }
            }
        }

        // Navigate to fragment_register
        binding.tvLinkToRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavControllerSafely()?.navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load account is remembered
        activity?.application?.apply {
            viewModel.viewModelScope.launch {
                applicationContext.dataStoreAccount.data.collect {
                    binding.edtPhoneNumber.setText(it[phoneNumberPreference].toString())
                    binding.edtPassword.setText(it[passwordPreference].toString())
                }
            }
        }
    }

    /**
     * This function is used to sign in with phone number
     *
     * @param phoneNumber
     */
    private fun signInWithPhoneNumber(phoneNumber: String) = activity?.apply {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(COUNTRY_PHONE_NUMBER + phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * This function is used to create credential and sign in with it
     *
     * @param code
     */
    private fun verifyOTPCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    /**
     * This function is used to show Dialog fragment confirm OTP code
     */
    private fun showFragmentConfirmOTPCode(verificationId: String) {
        if(auth.currentUser == null) {
            MyBaseDialogFragment(
                layoutId = R.layout.dialog_fragment_confirm_otp,
                title = "",
                bind = {
                    // Set event re-send OTP code
                    it.findViewById<AppCompatImageButton>(R.id.btnReSendOTPCode).setOnClickListener {
                        activity?.apply {
                            // Handle re-send OTP code
                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(COUNTRY_PHONE_NUMBER + binding.edtPhoneNumber.text.toString()) // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(this) // Activity (for callback binding)
                                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                                .setForceResendingToken(resendToken)
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        }
                    }
                },
                onPositionButtonClick = {
                    // Get OTP code entered
                    val txtOTPCode = it.findViewById<EditText>(R.id.edtOTPCode).text.toString()
                    // Verify OTP code
                    verifyOTPCode(verificationId, txtOTPCode)
                },
                onNegativeButtonClick = {}
            ).show(
                childFragmentManager, "Dialog Fragment: Confirm OTP code"
            )
        }
    }

    /**
     * This function is used to sign in with phone number
     *
     * @param credential
     */
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // if true, navigate up
                findNavControllerSafely()?.navigateUp()
            } else {
                // TODO: Handle error sign in
                Log.d(TAG, "onVerificationFailed: in sign in")
                // if false, notification error
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.txtLoginError))
//                        .setMessage(getString(R.string.txtAccountErrorMessage))
                    .setMessage("Has error: ${it.exception}")
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create()
                    .show()
            }
        }
    }

    /**
     * This function is used to save phone number and password to datastore
     */
    private fun rememberAccount(phoneNumber: String, password: String) {
        activity?.application?.apply {
            viewModel.viewModelScope.launch {
                applicationContext.dataStoreAccount.edit {
                    it[phoneNumberPreference] = phoneNumber
                    it[passwordPreference] = password
                }
            }
        }
    }
}