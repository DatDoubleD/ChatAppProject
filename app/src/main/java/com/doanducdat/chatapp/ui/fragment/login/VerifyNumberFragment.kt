package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

    private val dialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerifyNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getDataCode()
        //click verify: check OTP -> register credential
        binding.btnVerify.setOnClickListener {
            dialog.startLoadingDialog()
            if (checkOTP()) {
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
                    val user: User = User(
                        "", "", "", firebaseAuth.currentUser!!.phoneNumber!!,
                        firebaseAuth.currentUser!!.uid
                    )
                    db.child(firebaseAuth.uid!!).setValue(user)
                    dialog.stopLoadingDialog()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed, please check your OTP again!", Toast.LENGTH_LONG).show()
                dialog.stopLoadingDialog()
            }
    }

    private fun getDataCode() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            verificationId = bundle.getString("CODE")
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