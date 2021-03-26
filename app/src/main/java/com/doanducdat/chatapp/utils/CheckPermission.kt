package com.doanducdat.chatapp.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

object CheckPermission {

    fun requestPermissionImage(activity: Activity, REQUEST_PERMISSION_CODE: Int): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
            false
        }
    }
}