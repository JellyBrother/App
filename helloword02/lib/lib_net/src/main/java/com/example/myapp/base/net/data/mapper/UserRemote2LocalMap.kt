package com.example.myapp.base.net.data.mapper

import com.example.myapp.base.net.data.local.entity.UserEntity
import com.example.myapp.base.net.data.remote.response.UserResponse

class UserRemote2LocalMap : Mapper<UserResponse, UserEntity> {
    override fun map(input: UserResponse): UserEntity {
        return UserEntity(
            accountName = input.account?.username ?: "",
            nickName = input.account?.username ?: "",
            mobile = input.account?.telephone ?: "",
            umId = input.account?.account ?: "",
            email = input.account?.email ?: "",
            token = input.token ?: "",
            userId = input.account?.id ?: "",
            avatarUrl = input.account?.profilePic ?: "",
        )
    }
}