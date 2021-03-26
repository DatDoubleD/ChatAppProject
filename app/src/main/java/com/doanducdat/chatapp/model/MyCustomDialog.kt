package com.doanducdat.chatapp.model

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.doanducdat.chatapp.R


class MyCustomDialog(var activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun startLoadingDialog(idResource:Int, sizeWidth:Int, sizeHeight:Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.layoutInflater;
        builder.setView(inflater.inflate(idResource, null))
        builder.setCancelable(false)

        dialog = builder.create();
        dialog.show()
        dialog.window?.setLayout(sizeWidth, sizeHeight)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    fun stopLoadingDialog() {
        dialog.dismiss()
    }
}