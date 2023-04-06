package com.example.myapp.base.net.data.mapper

import com.example.myapp.base.net.data.local.entity.UserEntity
import com.example.myapp.base.net.user.UserInfo

class UserLocal2ModuleMap : Mapper<UserEntity, UserInfo> {
    override fun map(input: UserEntity): UserInfo {
        return UserInfo(
            accountName = input.accountName ?: "",
            nickName = input.nickName ?: "",
            mobile = input.mobile,
            umId = input.umId,
            token = input.token,
            jaToken = input.jaToken,
            userId = input.userId ?: "",
            avatarUrl = input.avatarUrl,
            isNewUser = input.isNewUser,
        )
    }
}