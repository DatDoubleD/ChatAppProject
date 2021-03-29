package com.doanducdat.chatapp.model

import java.io.Serializable

class Message(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var date: String = System.currentTimeMillis().toString(),
    var type: String = ""
):Serializable{

}