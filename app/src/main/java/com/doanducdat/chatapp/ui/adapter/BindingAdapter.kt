package com.doanducdat.chatapp.ui.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.Message
import com.doanducdat.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("app:setAvatar")
fun setAvatar(view: CircleImageView, img: String) {
    if (TextUtils.isEmpty(img)) {
        view.load(R.drawable.avatar)
    } else {
        view.load(img)
    }
}

@BindingAdapter("app:setImgMsg")
fun setImgMsg(imageView: ImageView, message: Message) {
    if (message.type.equals("image")) {
        imageView.visibility = View.VISIBLE
        imageView.load(message.message)
    }
}