package com.doanducdat.chatapp.utils

import com.google.firebase.auth.FirebaseAuth

class AppUtil {
    fun getUid(): String {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid!!
    }
}