package com.example.myapp.base.net.data.remote.response

import java.io.Serializable

data class UserResponse(
    val account: Account?,
    var token: String? = null,
) : Serializable {

    data class Account(
        val account: String = "",
        val createTime: String = "",
        val createUser: String = "",
        val email: String = "",
        val expireDate: String = "",
        val id: String = "",
        val profilePic: String = "",
        val status: Int = 0,
        val telephone: String = "",
        val updateTime: String = "",
        val updateUser: String = "",
        val username: String = ""
    ) : Serializable
}