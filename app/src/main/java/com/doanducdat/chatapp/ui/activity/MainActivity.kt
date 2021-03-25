package com.doanducdat.chatapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
    }

    private val controller by lazy { navHostFragment.findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btmNvgBarMain.setOnItemSelectedListener { id ->
            when (id) {
                R.id.mnu_chatFragment -> controller.navigate(R.id.chatFragment)
                R.id.mnu_contactFragment -> controller.navigate(R.id.contactFragment)
                R.id.mnu_profileFragment -> controller.navigate(R.id.profileFragment)
            }
        }
        binding.btmNvgBarMain.setItemSelected(R.id.mnu_chatFragment)
    }
}