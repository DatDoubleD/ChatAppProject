package com.doanducdat.chatapp.model

import java.io.Serializable

class User(
    var name: String,
    var status: String,
    var image: String,
    var phone: String,
    var birthYear:String,
    var uID:String
):Serializable {
}