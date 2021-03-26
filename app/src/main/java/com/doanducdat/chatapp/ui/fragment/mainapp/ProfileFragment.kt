package com.doanducdat.chatapp.ui.fragment.mainapp

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.databinding.CustomDialogStatusBinding
import com.doanducdat.chatapp.databinding.FragmentProfileBinding
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.viewmodel.ProfileViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var dialog: AlertDialog
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val myCustomDialog by lazy {
        MyCustomDialog(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        profileViewModel.getUser().observe(viewLifecycleOwner, {
            binding.user = it
        })

        binding.btnEditStatus.setOnClickListener {
            startLoadingDialog(750, 500)

        }

    }

    private fun startLoadingDialog(sizeWidth: Int, sizeHeight: Int) {

        val dialogBinding: CustomDialogStatusBinding = CustomDialogStatusBinding.inflate(
            LayoutInflater.from(requireContext()), null, false
        )

        dialogBinding.btnDone.setOnClickListener {
            updateStatus(dialogBinding.edtStatus.text.toString())
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        dialog = builder.create();
        dialog.show()
        dialog.window?.setLayout(sizeWidth, sizeHeight)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun updateStatus(status: String) {
        myCustomDialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)

        if (TextUtils.isEmpty(status)) {
            myCustomDialog.stopLoadingDialog()
            dialog.dismiss()
            Toast.makeText(requireContext(), "Your status is empty", Toast.LENGTH_SHORT).show()
        } else {
            myCustomDialog.stopLoadingDialog()
            dialog.dismiss()
            Toast.makeText(requireContext(), "Your status was updated", Toast.LENGTH_SHORT).show()
        }
    }
}