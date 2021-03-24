package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentVerifyNumberBinding
import com.doanducdat.chatapp.model.LoadingDialog
import com.doanducdat.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase

class VerifyNumberFragment : Fragment() {

    private lateinit var binding: FragmentVerifyNumberBinding
    private lateinit var codeOTP: String
    private var verificationId: String? = null
    private lateinit var user: User
    private val dialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerifyNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getIdAndInfoUser()
        //click verify: check OTP -> register credential
        binding.btnVerify.setOnClickListener {
            dialog.startLoadingDialog()
            if (checkOTP()) {
                //verificationID: OTP receive, codeOTP: OTP user retype to verify
                val credential = PhoneAuthProvider.getCredential(verificationId!!, codeOTP)
                registerUser(credential)
            }
        }
    }

    private fun registerUser(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance().getReference("users")

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user.phone = firebaseAuth.currentUser!!.phoneNumber!!
                    user.uID = firebaseAuth.currentUser!!.uid

                    db.child(firebaseAuth.uid!!).setValue(user)
                    dialog.stopLoadingDialog()
                    Toast.makeText(requireContext(), "Register successfully, you can use this account to Login!", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed, please check your OTP again!", Toast.LENGTH_LONG).show()
                dialog.stopLoadingDialog()
            }
    }

    private fun getIdAndInfoUser() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            verificationId = bundle.getString("CODE")
            user = bundle.getSerializable("INFO_USER") as User
        }
    }

    private fun checkOTP(): Boolean {
        //verify OTP ok -> use CODE register user in database
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