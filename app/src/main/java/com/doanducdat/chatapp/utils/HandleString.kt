package com.doanducdat.chatapp.utils

import java.util.*

class HandleString {
    companion object{
        fun standardizeString(str: String): String {
            var strStandardized = str
            //clear space
            strStandardized = strStandardized.trim()
            strStandardized = strStandardized.replace("\\s+".toRegex(), " ")
            //split
            val temp:List<String> = strStandardized.split(" ")
            strStandardized = ""
            for (i in temp.indices) {
                strStandardized += temp[i][0].toString().toUpperCase(Locale.ROOT) + temp[i].substring(1)
                    .toLowerCase(Locale.ROOT) + " "
            }
            return strStandardized
        }
    }
}