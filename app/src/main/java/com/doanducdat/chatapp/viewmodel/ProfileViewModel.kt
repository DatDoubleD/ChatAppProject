package com.doanducdat.chatapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.repository.AppRepository

class ProfileViewModel : ViewModel() {

    private var appRepository: AppRepository = AppRepository.getInstance()

    fun getUser(): LiveData<User> = appRepository.getUser()

    fun updateStatus(status:String) = appRepository.updateStatus(status)

    fun updateAvatar(bitmap: Bitmap) = appRepository.updateAvatar(bitmap)
}