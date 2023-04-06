package com.example.myapp.base.net.data.repository.java

import com.example.myapp.base.net.data.FlowHelper
import com.example.myapp.base.net.data.asApiResultFlow
import com.example.myapp.base.net.data.local.AppDatabase
import com.example.myapp.base.net.data.mapper.JaUserRemote2LocalMapper
import com.example.myapp.base.net.data.mapper.UserLocal2ModuleMap
import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.data.remote.api.java.JaUserApi
import com.example.myapp.base.net.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JaUserRepositoryImpl(
    val api: JaUserApi.Proxy,
    val db: AppDatabase,
    val jaUserRemote2LocalMapper: JaUserRemote2LocalMapper,
    val userLocal2ModuleMap: UserLocal2ModuleMap,
) : JaUserRepository {

    override suspend fun getUserInfo(userInfo: UserInfo?): Flow<ApiResult<UserInfo>> {
        return FlowHelper
            .asFlow {
                api.getUserInfo()
            }
            .map {
                val localData = jaUserRemote2LocalMapper.map(it).copy(
                    token = userInfo?.token ?: "",
                    jaToken = userInfo?.jaToken ?: ""
                )
                val userDao = db.userDao()
                if (userDao.getByUserId(localData.userId ?: "") == null) {
                    userDao.insert(localData)
                } else {
                    userDao.update(localData)
                }
                localData
            }
            .map {
                userLocal2ModuleMap.map(it)
            }
            .asApiResultFlow()
    }
}