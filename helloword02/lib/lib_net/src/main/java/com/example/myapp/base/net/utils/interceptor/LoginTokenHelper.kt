package com.example.myapp.base.net.utils.interceptor

import com.example.myapp.base.utils.JumpUtils
import com.example.myapp.base.utils.SPUtils
import org.json.JSONObject

/**
 * 因为ARouter初始化耗时，放在了子线程，如果用ARouter的跳转的话，可能导致ARouter没初始化完成，所有不用LoginService跨模块调用
 */
object LoginTokenHelper {
    // 用户
    const val FILE_USER = "file_user"
    const val USER_INFO = "user_info"
    const val USER_KEY_UID = "user_id"

    fun isLogin(): Boolean {
        return getUserInfoJsonObject() != null
    }

    fun getLoginToken(): String {
        return "bearer ${getUserInfoJsonObject()?.optString("token") ?: ""}"
    }

    fun getJaLoginToken(): String {
        return getUserInfoJsonObject()?.optString("jaToken") ?: ""
    }

    fun getUserId(): String {
        return getUserInfoJsonObject()?.optString("userId") ?: ""
    }

    fun loginTokenInvalidJump() {
        SPUtils.getInstance(FILE_USER).remove(
            USER_INFO
        )
        JumpUtils.jumpLoginPage(false)
    }

    private fun getUserInfoJsonObject(): JSONObject? {
        val text =
            SPUtils.getInstance(FILE_USER).getString(
                USER_INFO
            )
        return try {
            JSONObject(text)
        } catch (e: Exception) {
            null
        }
    }
}