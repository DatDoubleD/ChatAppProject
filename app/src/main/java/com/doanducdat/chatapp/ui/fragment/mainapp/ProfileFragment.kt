package com.doanducdat.chatapp.ui.fragment.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.FragmentProfileBinding
import com.doanducdat.chatapp.viewmodel.ProfileViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val profileViewModel: ProfileViewModel by activityViewModels()
        profileViewModel.getUser().observe(viewLifecycleOwner, {
            binding.user = it
        })
    }
}