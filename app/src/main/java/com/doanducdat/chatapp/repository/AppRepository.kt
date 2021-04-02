package com.doanducdat.chatapp.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doanducdat.chatapp.`interface`.MyCallback
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.utils.AppUtil
import com.doanducdat.chatapp.utils.HandleImage
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AppRepository {
    private var liveData: MutableLiveData<User> = MutableLiveData()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var database: DatabaseReference
    private val appUtil: AppUtil = AppUtil()

    companion object {
        private var instance: AppRepository? = null
        fun getInstance(): AppRepository {
            if (instance == null) {
                instance = AppRepository()
            }
            return instance!!
        }
    }

    fun getUser(): LiveData<User> {
        database = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    //setvalue only use to update object in Main Thread
                    liveData.postValue(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return liveData
    }

    fun updateStatus(status: String) {
        database = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        val map: Map<String, Any> = mapOf("status" to status)
        database.updateChildren(map)
    }

    fun updateAvatar(bitmap: Bitmap, result:MyCallback) {
        val avatarRef = storageRef.child("${appUtil.getUid()}/avatar.png")

        val bitmapResized: Bitmap = HandleImage.resizeBitmap(bitmap, 761, 761)

        var uploadTask = avatarRef.putBytes(HandleImage.fromBitmap(bitmapResized))

        uploadTask.addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                uploadLinkAvatarToUser(uri, result)
            }
        }

    }

    private fun uploadLinkAvatarToUser(uri: Uri, result: MyCallback) {
        database = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        val map: Map<String, Any> = mapOf("image" to uri.toString())
        database.updateChildren(map).addOnCompleteListener {
            result.resultUpdateAvatar(true)
        }.addOnFailureListener {
            result.resultUpdateAvatar(false)
        }
    }
}