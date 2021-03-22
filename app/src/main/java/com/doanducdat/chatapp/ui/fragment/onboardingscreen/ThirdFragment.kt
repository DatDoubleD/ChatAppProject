package com.doanducdat.chatapp.ui.fragment.onboardingscreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentThirdBinding
import com.doanducdat.chatapp.ui.activity.LoginActivity
import com.doanducdat.chatapp.ui.activity.MainActivity


class ThirdFragment : Fragment() {

    private lateinit var binding: FragmentThirdBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}