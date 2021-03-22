package com.doanducdat.chatapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.doanducdat.chatapp.ui.fragment.login.LoginFragment
import com.doanducdat.chatapp.ui.fragment.login.RegisterFragment

class LoginAndRegisterAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return LoginFragment()
            1 -> return RegisterFragment()
        }
        return LoginFragment()
    }
}