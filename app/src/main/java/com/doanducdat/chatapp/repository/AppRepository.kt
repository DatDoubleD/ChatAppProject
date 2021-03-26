package com.doanducdat.chatapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doanducdat.chatapp.model.User
import com.doanducdat.chatapp.utils.AppUtil
import com.google.firebase.database.*

class AppRepository {
    private var liveData: MutableLiveData<User> = MutableLiveData()

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
                TODO("Not yet implemented")
            }
        })
        return liveData
    }

    fun updateStatus(status:String)  {
        var resultUpdate:Boolean = false
        database = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUid())
        val map:Map<String, Any> = mapOf("status" to status)
        database.updateChildren(map)
    }
}