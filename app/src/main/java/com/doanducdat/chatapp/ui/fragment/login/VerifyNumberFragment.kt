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
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*

class VerifyNumberFragment : Fragment() {

    private lateinit var binding: FragmentVerifyNumberBinding
    private lateinit var codeOTP: String
    private var verificationId: String? = null
    private lateinit var user: User

    private val dialog: MyCustomDialog by lazy {
        MyCustomDialog(requireActivity())
    }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getIdAndInfoUser()
        //click verify: check OTP -> register credential
        binding.btnVerify.setOnClickListener {
            dialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)
            if (checkOTP()) {
                //verificationID: OTP receive, codeOTP: OTP user retype to verify
                val credential = PhoneAuthProvider.getCredential(verificationId!!, codeOTP)
                registerUser(credential)
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

    private fun registerUser(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) { // khớp OTP -> check use if exits?
                    user.phone = firebaseAuth.currentUser!!.phoneNumber!!
                    user.uID = firebaseAuth.currentUser!!.uid
                    checkUserIDExit(firebaseAuth.currentUser!!.uid)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(), // chưa check internet
                    "Failed, please check your OTP again!",
                    Toast.LENGTH_LONG
                ).show()
                dialog.stopLoadingDialog()
            }
    }

    private fun checkUserIDExit(uid: String) {
        //register
        db.child("users").child(uid).get().addOnSuccessListener {
            //exit -> fail, not exit -> create new user
            if (it.exists()) {
                dialog.stopLoadingDialog()
                Toast.makeText(
                    requireContext(),
                    "Fail, this phone number is already in use with another account!",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            } else {
                createNewUserToDatabase()
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

    private fun createNewUserToDatabase() {
        db.child("users").child(firebaseAuth.uid!!).setValue(user)
            .addOnCompleteListener {
                dialog.stopLoadingDialog()
                Toast.makeText(
                    requireContext(),
                    "Successfully, You can login with this phone!",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                dialog.stopLoadingDialog()
                Toast.makeText(
                    requireContext(),
                    "Fail, an error occurred. Please try again later",
                    Toast.LENGTH_LONG
                ).show()
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