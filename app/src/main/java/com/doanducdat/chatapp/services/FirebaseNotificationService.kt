package com.doanducdat.chatapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.ui.fragment.mainapp.ViewSendMsgFragment
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

            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val partnerUserId = map["partnerUserID"]
            val partnerUserImage = map["partnerUserImage"]
            val chatId = map["chatID"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title!!, message!!, partnerUserId!!, partnerUserImage!!, chatId!!)
            else createNormalNotification(title!!, message!!, partnerUserId!!, partnerUserImage!!, chatId!!)

        }

    }

    private fun updateToken(token: String) {

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        val map: MutableMap<String, Any> = hashMapOf("token" to token)
        databaseReference.updateChildren(map)

    }

    private fun createNormalNotification(
        title: String,
        message: String,
        partnerUserId: String,
        partnerUserImage: String,
        chatId: String
    ) {
        val intent = Intent(this, ViewSendMsgFragment::class.java)
        intent.putExtra("hisId", partnerUserId)
        intent.putExtra("hisImage", partnerUserImage)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, Appconstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.purple_500, null))
            .setSound(uri)
            .setContentIntent(pendingIntent)




        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        partnerUserId: String,
        partnerUserImage: String,
        chatId: String
    ) {

        val channel = NotificationChannel(
            Appconstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val intent = Intent(this, ViewSendMsgFragment::class.java)
        intent.putExtra("hisId", partnerUserId)
        intent.putExtra("hisImage", partnerUserImage)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, Appconstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.purple_500, null))
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        manager.notify(100, notification)
    }
}