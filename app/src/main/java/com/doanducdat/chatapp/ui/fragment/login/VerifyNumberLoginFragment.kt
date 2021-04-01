package com.doanducdat.chatapp.ui.fragment.login

import android.content.Intent
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
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.activity.MainActivity
import com.doanducdat.chatapp.utils.AppUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging


class VerifyNumberLoginFragment : Fragment() {

    private lateinit var binding:FragmentVerifyNumberLoginBinding
    private var verificationId: String? = null
    private lateinit var user: User
    private lateinit var codeOTP: String

    private val dialog: MyCustomDialog by lazy { MyCustomDialog(requireActivity()) }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }
    private val appUtil = AppUtil()
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
            dialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)
            if (checkOTP()){
                val credential = PhoneAuthProvider.getCredential(verificationId!!, codeOTP)
                login(credential)
            }else{
                dialog.stopLoadingDialog()
                Toast.makeText(
                    requireContext(),
                    "Failed, please check your OTP again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun login(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) { //khớp OTP -> check if phone number was register?
                    checkPhoneNumberIsRegister(firebaseAuth.currentUser!!.uid)
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed, please check your OTP again!", Toast.LENGTH_LONG).show()
                dialog.stopLoadingDialog()
            }
    }

    private fun checkPhoneNumberIsRegister(uid: String) {
        db.child("users").child(uid).get().addOnSuccessListener {
            //exit -> login , not exit -> msg: not register
            if (it.exists()) {
                dialog.stopLoadingDialog()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                dialog.stopLoadingDialog()
                Toast.makeText(
                    requireContext(),
                    "Fail, this phone number is not registered!",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }

        }.addOnFailureListener {
            dialog.stopLoadingDialog()
            Toast.makeText(
                requireContext(),// chưa check internet
                "Fail, an error occurred. Please try again later",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateTokenUser() {

       FirebaseMessaging.getInstance().token.addOnCompleteListener {
           if (it.isSuccessful){
               val token = it.result
               val dbRef = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
               val map:Map<String, Any> = mapOf("token" to token!!)
               dbRef.updateChildren(map)
           }
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