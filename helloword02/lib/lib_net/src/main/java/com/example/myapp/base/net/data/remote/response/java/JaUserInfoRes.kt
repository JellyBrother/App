package com.example.myapp.base.net.data.remote.response.java

data class JaUserInfoRes(
    val account: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val nickName: String? = null,
    val sex: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userType: String? = null,
    val avatar: String? = null,
    val token: String? = null,
    // 平台Token
    val ptToken: String? = null
)