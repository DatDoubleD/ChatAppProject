package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentLoginBinding
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.utils.HandlePhone
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private var validPhone: Boolean = false
    private var verificationId: String? = null

    private val navHostFragment by lazy {
        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val controller by lazy { navHostFragment.findNavController() }
    private val dialog: MyCustomDialog by lazy {
        MyCustomDialog(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.cppLogin.registerCarrierNumberEditText(binding.txtInputEdtPhone)
        checkLengthNumberPhone()
        checkValidNumberPhone()
        binding.btnLogin.setOnClickListener {
            checkInFoUserToLogin()
        }

    }


    private fun checkInFoUserToLogin() {
        dialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)
        if (!validPhone) {
            dialog.stopLoadingDialog()
            Toast.makeText(requireContext(), "Your Phone is invalid!", Toast.LENGTH_LONG).show()
        }else{
            val phoneNumber:String = binding.cppLogin.selectedCountryCodeWithPlus + binding.txtInputEdtPhone.text.toString()
            //chưa check xem phone có tồn tại trong database chưa!!!
            HandlePhone.generateOTP(phoneNumber, requireActivity(), initCallbackToListenOTP())
        }
    }
    private fun initCallbackToListenOTP(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

                override fun onVerificationFailed(p0: FirebaseException) {
                    if (p0 is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        dialog.stopLoadingDialog()
                        Toast.makeText(requireContext(), "${p0.message}", Toast.LENGTH_LONG).show()
                    } else if (p0 is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        dialog.stopLoadingDialog()
                        Toast.makeText(requireContext(), "${p0.message}", Toast.LENGTH_LONG).show()
                    }
                    dialog.stopLoadingDialog()
                    Toast.makeText(requireContext(), "${p0.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationCode: String, p1: PhoneAuthProvider.ForceResendingToken) {

                    verificationId = verificationCode // code use : register user after verify OTP
                    val bundle: Bundle = bundleOf("CODE" to verificationId)

                    dialog.stopLoadingDialog()
                    controller.navigate(R.id.verifyNumberLoginFragment, bundle)
                }
            }
        return callbacks
    }

    private fun checkLengthNumberPhone() {
        binding.txtInputEdtPhone.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20) {
                binding.txtInputLayout.error = "No More!"
            } else if (text.length < 20) {
                binding.txtInputLayout.error = null
            }
        }
    }
    private fun checkValidNumberPhone() {
        binding.cppLogin.setPhoneNumberValidityChangeListener { isValid ->
            validPhone = if (isValid) {
                binding.imgValid.setImageResource(R.drawable.ic_valid)
                true
            } else {
                binding.imgValid.setImageResource(R.drawable.ic_unvalid)
                false
            }
        }
    }
}