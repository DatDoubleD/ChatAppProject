package com.doanducdat.chatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.fragment.mainapp.ViewSendMsgFragment

class HandlerReceiveNotificaitonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_receive_notificaiton)
        getDataFromNotification()
    }

    private fun getDataFromNotification() {
        val intent = Intent()
        val partnerUser: User = intent.getSerializableExtra("PARTNER_USER") as User
        val myUser:User = intent.getSerializableExtra("MY_USER") as User
        val chatID: String? = intent.getStringExtra("chatID")
        val bundle: Bundle = bundleOf("PARTNER_USER" to partnerUser, "MY_USER" to myUser, "chatID" to chatID)

        val viewSendMsgFragment: ViewSendMsgFragment = ViewSendMsgFragment()
        viewSendMsgFragment.arguments = bundle
        this.supportFragmentManager.beginTransaction()
            .replace(R.id.container_display_notify, viewSendMsgFragment)
            .commit()

    }
}