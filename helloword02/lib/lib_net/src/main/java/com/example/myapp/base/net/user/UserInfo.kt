package com.example.myapp.base.net.user

import com.example.myapp.base.constant.BaseConstant
import java.io.Serializable

data class UserInfo(
    val userId: String,
    // 账户名，等同于后端的userName
    val accountName: String,
    val nickName: String,
    val mobile: String? = null,
    val umId: String? = null,
    val avatarUrl: String? = null,
    val token: String? = null,
    val jaToken: String? = null,
    val isNewUser: Boolean = false,
) : Serializable {
    fun getBearerToken(): String {
        return BaseConstant.HttpConfig.TOKEN_BEARER + token
    }

    fun getBasicToken(): String {
        return BaseConstant.HttpConfig.TOKEN_BASIC + token
    }

    fun getRoleString(): String {
        return "1"
    }
}
