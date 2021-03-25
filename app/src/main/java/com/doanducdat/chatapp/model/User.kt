package com.doanducdat.chatapp.model

import java.io.Serializable

class User(
    var name: String? = null,
    var status: String? =  null,
    var image: String? = null,
    var phone: String? = null,
    var birthYear:String? = null,
    var uID:String? = null
):Serializable {

}