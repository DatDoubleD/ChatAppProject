package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentVerifyNumberLoginBinding
import com.doanducdat.chatapp.model.LoadingDialog
import com.doanducdat.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase


class VerifyNumberLoginFragment : Fragment() {

    private lateinit var binding:FragmentVerifyNumberLoginBinding
    private var verificationId: String? = null
    private lateinit var user: User
    private lateinit var codeOTP: String
    private val dialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyNumberLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getID()
        binding.btnVerifyLogin.setOnClickListener {
            dialog.startLoadingDialog()
            if (checkOTP()){
                val credential = PhoneAuthProvider.getCredential(verificationId!!, codeOTP)
                Login(credential)
            }
        }
    }
    private fun Login(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog.stopLoadingDialog()
                    Toast.makeText(requireContext(), firebaseAuth.currentUser!!.uid, Toast.LENGTH_LONG).show()

                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed, please check your OTP again!", Toast.LENGTH_LONG).show()
                dialog.stopLoadingDialog()
            }
    }
    private fun getID() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            verificationId = bundle.getString("CODE")
        }
    }
    private fun checkOTP(): Boolean {
        //verify OTP ok -> use CODE login
        codeOTP = binding.pinViewOtp.text.toString()

        if (TextUtils.isEmpty(codeOTP)) {
            binding.pinViewOtp.error = "OTP is required"
            return false
        } else if (codeOTP.length < 6) {
            binding.pinViewOtp.error = "enter valid OTP"
            return false
        }
        return true
    }
}