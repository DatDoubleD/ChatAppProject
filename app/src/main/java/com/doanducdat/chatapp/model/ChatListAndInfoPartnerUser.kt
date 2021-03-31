package com.doanducdat.chatapp.model

import java.io.Serializable

class ChatListAndInfoPartnerUser(
    var chatID: String ="",
    var name: String= "",
    var lastMessage: String = "",
    var image: String = "",
    var date: String ="",
    var online:String ="" //here is status on/offline of partnerUser
) : Serializable {
}