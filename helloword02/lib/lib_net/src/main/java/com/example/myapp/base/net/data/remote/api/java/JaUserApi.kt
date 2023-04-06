package com.example.myapp.base.net.data.remote.api.java

import com.example.myapp.base.net.data.remote.response.java.JaUserInfoRes
import com.example.myapp.base.net.utils.NetworkResponse
import retrofit2.http.GET

interface JaUserApi {

    /**
     * 查询用户信息
     */
    @GET("api/v1/user/userInfo")
    suspend fun getUserInfo(): NetworkResponse<JaUserInfoRes>

    class Proxy(val api: JaUserApi) {
        /**
         * 查询用户信息
         */
        suspend fun getUserInfo(): NetworkResponse<JaUserInfoRes> {
            return api.getUserInfo()
        }
    }
}