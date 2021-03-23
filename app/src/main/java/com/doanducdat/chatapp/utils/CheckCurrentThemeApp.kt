package com.doanducdat.chatapp.utils

import android.content.res.Configuration
import androidx.core.content.ContextCompat
import com.doanducdat.chatapp.R

class CheckCurrentThemeApp {
    companion object {
        /***
         * Light Theme -> return true, Night Theme -> return false
         */
        fun checkCurrentTheme(): Boolean {
            val currentNightMode = Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    return true
                }
            }
            return false
        }
    }
}