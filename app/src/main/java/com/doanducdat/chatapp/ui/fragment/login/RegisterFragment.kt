package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentRegisterBinding
import com.doanducdat.chatapp.model.LoadingDialog
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.utils.HandlePhone
import com.doanducdat.chatapp.utils.HandleString
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var validPhone: Boolean = false
    private var verificationId: String? = null

    private val navHostFragment by lazy {
        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val controller by lazy { navHostFragment.findNavController() }

    private val dialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

    private lateinit var userName:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //combine txtinnputedt with contrycountpicker
        binding.cppRegister.registerCarrierNumberEditText(binding.txtInputEdtPhone)
        checkLengthNumberPhone()
        checkLengthName()
        checkValidNumberPhone()
        //
        binding.btnRegister.setOnClickListener {
            checkInFoUserToRegister()
        }
    }

    private fun checkInFoUserToRegister() {
        dialog.startLoadingDialog()
        if (!validPhone) {
            dialog.stopLoadingDialog()
            Toast.makeText(requireContext(), "Your Phone is invalid!", Toast.LENGTH_LONG).show()
        }
        else if (TextUtils.isEmpty(binding.txtInputEdtName.text.toString())) {
            dialog.stopLoadingDialog()
            Toast.makeText(requireContext(), "Your Name is empty!", Toast.LENGTH_LONG).show()
        }
        else {
            userName = HandleString.standardizeString(binding.txtInputEdtName.text.toString())
            binding.txtInputEdtName.setText(userName)
            val phoneNumber:String = binding.cppRegister.selectedCountryCodeWithPlus + binding.txtInputEdtPhone.text.toString()
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
                    val user:User = User(userName, "", "", "", "", "")
                    val bundle: Bundle = bundleOf("CODE" to verificationId, "INFO_USER" to user)

                    dialog.stopLoadingDialog()
                    controller.navigate(R.id.verifyNumberFragment, bundle)
                }
            }
        return callbacks
    }


    private fun checkValidNumberPhone() {
        binding.cppRegister.setPhoneNumberValidityChangeListener { isValid ->
            validPhone = if (isValid) {
                binding.imgValid.setImageResource(R.drawable.ic_valid)
                true
            } else {
                binding.imgValid.setImageResource(R.drawable.ic_unvalid)
                false
            }
        }
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

    private fun checkLengthName() {
        binding.txtInputEdtName.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 30) {
                binding.txtInputLayout.error = "No More!"
            } else if (text.length < 30) {
                binding.txtInputLayout.error = null

            }
        }
    }
}
