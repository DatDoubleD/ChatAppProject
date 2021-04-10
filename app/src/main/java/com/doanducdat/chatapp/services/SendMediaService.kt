package com.doanducdat.chatapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.doanducdat.chatapp.R
import com.doanducdat.chatapp.model.Message
import com.doanducdat.chatapp.utils.AppUtil
import com.doanducdat.chatapp.utils.HandleImage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SendMediaService : Service() {
    private var chatID: String = ""
    private var partnerUserID: String = ""
    private val appUtil = AppUtil()
    private var listPickedImgs: ArrayList<String>? = null
    private var MAX_PROGRESS = 0
    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        partnerUserID = intent!!.getStringExtra("partnerUserID").toString()
        chatID = intent.getStringExtra("chatID").toString()
        listPickedImgs = intent.getStringArrayListExtra("MEDIA")
        MAX_PROGRESS = listPickedImgs!!.size

        startForeground(100, getNotification().build())

        for (i in listPickedImgs!!.indices) {
            //uri -> bitmap -> upload bitmap firestore
            val uri: Uri = Uri.parse(listPickedImgs!![i])
            val bitmap = HandleImage
                .resizeBitmap(HandleImage.toBitmapFromUri(uri, this.contentResolver), 100, 100)

            uploadImgMsg(bitmap)
            builder.setProgress(MAX_PROGRESS, i + 1, false)
            manager.notify(600, builder.build())
        }

        builder.setContentTitle("Sending Complete")
            .setProgress(MAX_PROGRESS, MAX_PROGRESS, false)
        manager.notify(600, builder.build())
        stopSelf()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun uploadImgMsg(bitmap: Bitmap) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imgNewMsg =
            storageReference.child("${appUtil.getUid()}/$chatID/media/images/${System.currentTimeMillis()}/")

        var uploadTask = imgNewMsg.putBytes(HandleImage.fromBitmap(bitmap))

        uploadTask.addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { url ->
                uploadNewMsg(url)
            }
        }

    }

    private fun uploadNewMsg(url: Uri?) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatID)
        val newMsgWithImage =
            Message(
                appUtil.getUid(),
                partnerUserID,
                url.toString(),
                System.currentTimeMillis().toString(),
                "image"
            )
        databaseReference.push().setValue(newMsgWithImage)
    }

    private fun getNotification(): NotificationCompat.Builder {
        builder = NotificationCompat.Builder(this, "android")
            .setContentText("Sending Media")
            .setProgress(MAX_PROGRESS, 0, false)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_notify)

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("android", "Message", NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lightColor =
                ResourcesCompat.getColor(resources, R.color.colorOfgetstarted, null)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            channel.description = "Sending Media"
            manager.createNotificationChannel(channel)
        }
        manager.notify(600, builder.build())
        return builder
    }

}