package com.doanducdat.chatapp.ui.fragment.mainapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import coil.load
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.`interface`.MyCallback
import com.doanducdat.chatapp.databinding.FragmentViewAvatarBinding
import com.doanducdat.chatapp.model.MyCustomDialog
import com.doanducdat.chatapp.utils.CheckPermission
import com.doanducdat.chatapp.viewmodel.ProfileViewModel
import com.google.firebase.database.Transaction

class ViewAvatarFragment : Fragment() {

    private lateinit var binding: FragmentViewAvatarBinding
    private lateinit var linkImage: String
    private val profileModel: ProfileViewModel by activityViewModels()
    private val myCustomDialog: MyCustomDialog by lazy { MyCustomDialog(requireActivity()) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLinkAvatarToLoad()
        binding.btnPickImage.setOnClickListener {
            if (CheckPermission.requestPermissionImage(requireActivity(), 100)) {
                pickImage()
            }
        }
        binding.btnUpdateAvatar.setOnClickListener {
            updateAvatar()
        }
        binding.btnBack.setOnClickListener {
            closeThisFragment()
        }
    }
    private fun closeThisFragment(){
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()

    }
    private fun updateAvatar() {
        myCustomDialog.startLoadingDialog(R.layout.custom_dialog, 200, 250)
        val bitmap: Bitmap = (binding.photoViewAvatar.drawable as BitmapDrawable).bitmap
        profileModel.updateAvatar(bitmap, object : MyCallback {
            override fun resultUpdateAvatar(result: Boolean) {
                if (result) {
                    myCustomDialog.stopLoadingDialog()
                    Log.d("AVATAR", "true")
                    closeThisFragment()
                } else {
                    myCustomDialog.stopLoadingDialog()
                    Log.d("AVATAR", "false")
                    closeThisFragment()
                }
            }
        })
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select image to upload"), 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            binding.photoViewAvatar.load(data.data)
        }
    }

    private fun getLinkAvatarToLoad() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            linkImage = bundle.getString("LINK_AVATAR").toString()
            binding.photoViewAvatar.load(linkImage)
        }
    }
}