package com.doanducdat.chatapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import coil.imageLoader
import coil.request.ImageRequest
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.ui.activity.HandlerReceiveNotificaitonActivity
import com.doanducdat.chatapp.utils.AppUtil
import com.doanducdat.chatapp.utils.Appconstants
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


//register service in manifest
class FirebaseNotificationService : FirebaseMessagingService() {
    private val appUtil = AppUtil()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, Any> = remoteMessage.data

            val title = map["title"].toString()
            val message = map["message"].toString()
            val partnerUser = User(
                map["partnerUser_name"].toString(),
                map["partnerUser_status"].toString(),
                map["partnerUser_image"].toString(),
                map["partnerUser_phone"].toString(),
                map["partnerUser_birthYear"].toString(),
                map["partnerUser_uID"].toString(),
                map["partnerUser_online"].toString(),
            )

            val myUser = User(
                map["myUser_name"].toString(),
                map["myUser_status"].toString(),
                map["myUser_image"].toString(),
                map["myUser_phone"].toString(),
                map["myUser_birthYear"].toString(),
                map["myUser_uID"].toString(),
                map["myUser_online"].toString(),
            )
            val chatId = map["chatID"].toString()

            createOreoNotification(title, message, partnerUser, myUser, chatId)

        }

    }

    private fun updateToken(token: String) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        val map: MutableMap<String, Any> = hashMapOf("token" to token)
        databaseReference.updateChildren(map)

    }


    private fun createOreoNotification(
        title: String,
        message: String,
        partnerUser: User,
        myUser: User,
        chatId: String
    ) {
        //pending intent
        val intent = Intent(this, HandlerReceiveNotificaitonActivity::class.java)
        intent.putExtra("PARTNER_USER", partnerUser)
        intent.putExtra("MY_USER", myUser)
        intent.putExtra("chatID", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        //sound
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, Appconstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notify)
            .setAutoCancel(true)
            .setSound(uri)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorOfgetstarted, null))
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //chanel
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                Appconstants.CHANNEL_ID,
                "Message",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            manager.createNotificationChannel(channel)
        }
        manager.notify(100, builder.build())
    }
}