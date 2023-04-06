package com.example.myapp.base.net.data.mapper

import com.example.myapp.base.net.data.local.entity.UserEntity
import com.example.myapp.base.net.data.remote.response.java.JaUserInfoRes

class JaUserRemote2LocalMapper : Mapper<JaUserInfoRes, UserEntity> {
    override fun map(input: JaUserInfoRes): UserEntity {
        return UserEntity(
            accountName = input.userName ?: "",
            nickName = input.nickName ?: "",
            mobile = input.mobile ?: "",
            umId = input.account ?: "",
            email = "",
            token = input.ptToken ?: "",
            jaToken = input.token ?: "",
            userId = input.userId ?: "",
            avatarUrl = input.avatar ?: "",
        )
    }
}