package com.doanducdat.chatapp.ui.adapter

import android.text.TextUtils
import androidx.databinding.BindingAdapter
import coil.load
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("app:setAvatar")
fun setAvatar(view: CircleImageView, user: User){
    if (TextUtils.isEmpty(user.image) || user.image == null){
        view.load(R.drawable.avatar)
    }else{
        view.load(user.image)
    }
}