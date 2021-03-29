package com.doanducdat.chatapp.ui.adapter

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("app:setAvatar")
fun setAvatar(view: CircleImageView, img:String){
    if (TextUtils.isEmpty(img) ){
        view.load(R.drawable.avatar)
    }else{
        view.load(img)
    }
}
@BindingAdapter("imageMessage")
fun imageMessage(imageView:ImageView, image:String){
    if (image != null) {
        imageView.load(image)
    }
}