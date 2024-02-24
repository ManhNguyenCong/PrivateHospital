package com.example.privatehospital.homescreen.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.privatehospital.R
import com.example.privatehospital.databinding.FragmentRegisterBinding
import com.example.privatehospital.homescreen.ui.fragment.LoginFragment.Companion.dataStoreAccount
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModel
import com.example.privatehospital.homescreen.viewmodel.HospitalViewModelFactory
import com.example.privatehospital.util.COUNTRY_PHONE_NUMBER
import com.example.privatehospital.util.MyBaseDialogFragment
import com.example.privatehospital.util.TAG
import com.example.privatehospital.util.findNavControllerSafely
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment() {

    /**
     * This is used for binding to fragment_register
     */
    private lateinit var binding: FragmentRegisterBinding

    /**
     * This is a View Model which is used to handle data before display them
     */
    private val viewModel: HospitalViewModel by activityViewModels {
        HospitalViewModelFactory(requireContext())
    }

    /**
     * This is FirebaseAuth
     */
    private val auth = Firebase.auth

    /**
     * This is used to store verificationId when register
     */
    private lateinit var storedVerificationId: String

    /**
     * This argument is used to store resend Token
     */
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    /**
     * Object callbacks when verify phone number
     */
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            registerNewAccountWithPhoneNumber(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid request
                    Log.e(TAG, "onVerificationFailed: " + e.message)
                }

                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                    Log.e(TAG, "onVerificationFailed: " + e.message)
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity
                    Log.e(TAG, "onVerificationFailed: " + e.message)
                }
            }

            // Show a message and update the UI
            Toast.makeText(requireContext(), "Have an error: ${e.message}", Toast.LENGTH_SHORT)
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

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            showFragmentConfirmOTPCode()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.bind(
            inflater.inflate(R.layout.fragment_register, container, false)
        )

        // Clear menu
        activity?.findViewById<Toolbar>(R.id.toolbar)?.menu?.clear()

        // Navigate up to fragment_login
        binding.tvLinkToLogin.setOnClickListener {
            findNavControllerSafely()?.navigateUp()
        }

        // Enable btnRegister when confirm terms
        binding.cbxIsAgreed.setOnClickListener {
            binding.btnRegister.isEnabled = binding.cbxIsAgreed.isChecked
        }

        binding.btnRegister.setOnClickListener {
            if (validEntry()) {
                verifyPhoneNumber(binding.edtPhoneNumber.text.toString())
            }
        }

        return binding.root
    }

    /**
     * This function is used to validate entry of register form
     */
    private fun validEntry(): Boolean {
        // Get phone number
        val phoneNumber = binding.edtPhoneNumber.text.toString()
        // Get password
        val password = binding.edtPassword.text.toString()
        // Get re-password
        val rePassword = binding.edtRePassword.text.toString()

        /**
         * This argument is used to save result when validate inputs
         */
        var isCorrected = true

        // Validate phone number
        if (!viewModel.validEntryPhoneNumber(phoneNumber)) {
            // Set error log
            binding.edtPhoneNumber.error = getString(R.string.txtPhoneNumberIsEmpty)
            // Set isn't corrected
            isCorrected = false
        }
        // Validate password
        if (!viewModel.validEntryPassword(password)) {
            // Set error log
            binding.edtPassword.error = getString(R.string.txtPasswordNoCorrect)
            // Set isn't corrected
            isCorrected = false
        }
        // Validate re-password
        if (!viewModel.validRePassword(password, rePassword)) {
            // Set error log
            binding.edtRePassword.error = getString(R.string.txtRePasswordNoCorrect)
            // Set isn't corrected
            isCorrected = false
        }

        return isCorrected
    }

    /**
     * This function is used to send OTP code and check it
     *
     * @param phoneNumber
     */
    private fun verifyPhoneNumber(phoneNumber: String) = activity?.apply {
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
    private fun verifyOTPCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        registerNewAccountWithPhoneNumber(credential)
    }

    /**
     * This function is used to show Dialog fragment confirm OTP code
     */
    private fun showFragmentConfirmOTPCode() {
        if(auth.currentUser == null) {
            MyBaseDialogFragment(
                layoutId = R.layout.dialog_fragment_confirm_otp,
                title = "",
                bind = {
                    it.findViewById<AppCompatImageButton>(R.id.btnReSendOTPCode).setOnClickListener {
                        // Handle resend OTP code
                        activity?.apply {
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
                    val txtOTPCode = it.findViewById<EditText>(R.id.edtOTPCode).text.toString()

                    // Verify OTP code
                    verifyOTPCode(txtOTPCode)
                },
                onNegativeButtonClick = {}
            ).show(
                childFragmentManager, "Dialog Fragment: Confirm OTP code"
            )
        }
    }

    /**
     * This functions is used to sign in with PhoneAuthCredential,
     * if success, if unsuccessful show log error
     */
    private fun registerNewAccountWithPhoneNumber(credential: PhoneAuthCredential) =
        activity?.apply {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get phone number and password
                        val phoneNumber = binding.edtPhoneNumber.text.toString()
                        val password = binding.edtPassword.text.toString()

                        // Save auto retrieved SMS code
                        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
                            phoneNumber,
                            credential.smsCode
                        )

                        // Create a new user
                        auth.currentUser?.apply {
                            viewModel.addNewUser(uid, phoneNumber, password)
                        }

                        // Remember account
                        viewModel.viewModelScope.launch {
                            requireContext().dataStoreAccount.edit {
                                it[LoginFragment.phoneNumberPreference] = phoneNumber
                                it[LoginFragment.passwordPreference] = password
                            }
                        }

                        // Set status register is true
                        viewModel.registerStatus.value = true

                        // Navigation up to fragment login
                        findNavControllerSafely()?.navigateUp()
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            AlertDialog.Builder(context)
                                .setTitle(getString(R.string.txtOTPErrorTitle).uppercase())
                                .setMessage(getString(R.string.txtOTPErrorContent))
                                .setNegativeButton("Cancel") { _, _ ->
                                    showFragmentConfirmOTPCode()
                                }
                                .create().show()
                        } else {
                            Toast.makeText(requireContext(), "Time out!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
}