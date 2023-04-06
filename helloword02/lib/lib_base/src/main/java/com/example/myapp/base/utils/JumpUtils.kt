package com.example.myapp.base.utils

import android.content.Intent

object JumpUtils {
    fun jumpLoginPage(isClearTask: Boolean) {
        if (ActivityUtils.getTopActivity()::class.java.name == "com.example.myapp.user.ui.activity.login.UmLoginActivity") {
            return
        }
        val intent = Intent().apply {
            setClassName(
                "com.example.myapp",
                "com.example.myapp.user.ui.activity.login.UmLoginActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (isClearTask) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        Utils.getApp().startActivity(intent)
    }
}