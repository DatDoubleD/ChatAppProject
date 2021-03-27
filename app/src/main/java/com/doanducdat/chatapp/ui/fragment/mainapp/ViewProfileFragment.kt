package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentViewProfileBinding
import com.doanducdat.chatapp.model.User
import com.google.firebase.database.*


class ViewProfileFragment : Fragment() {

    private lateinit var binding: FragmentViewProfileBinding
    private lateinit var userLocal: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        getData()//get local user from app
        getUserInfoViewProfile()// qery that user from local user
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnBack.setOnClickListener {
            closeThisFragment()
        }

    }

    private fun closeThisFragment() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    private fun getData() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            userLocal = bundle.getSerializable("INFO_USER") as User
        }
    }

    private fun getUserInfoViewProfile() {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userLocal.uID.toString())
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    binding.user = user
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error! please try again later", Toast.LENGTH_SHORT).show()
            }
        })
    }

}