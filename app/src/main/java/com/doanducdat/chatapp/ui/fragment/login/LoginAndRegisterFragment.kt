package com.doanducdat.chatapp.ui.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentLoginAndRegisterBinding
import com.doanducdat.chatapp.ui.adapter.LoginAndRegisterAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class LoginAndRegisterFragment : Fragment() {

    private lateinit var binding: FragmentLoginAndRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginAndRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //set adapter for viewpager2
        val adapter: LoginAndRegisterAdapter = LoginAndRegisterAdapter(requireActivity())
        binding.viewPager2LoginAndRegister.adapter = adapter

        //set up tablayout by tabLayoutMediator
        TabLayoutMediator(binding.tabLayoutLoginAndRegister, binding.viewPager2LoginAndRegister)
        { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Login"
                }
                1 -> {
                    tab.text = "Register"
                }
            }
        }.attach()
    }
}