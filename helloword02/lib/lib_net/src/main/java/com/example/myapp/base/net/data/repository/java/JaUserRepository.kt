package com.example.myapp.base.net.data.repository.java

import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface JaUserRepository {
    /**
     * 查询用户信息
     */
    suspend fun getUserInfo(userInfo: UserInfo?): Flow<ApiResult<UserInfo>>
}