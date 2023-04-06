package com.example.myapp.base.net.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var accountName: String?,
    var nickName: String?,
    var mobile: String?,
    var umId: String? = null,
    var email: String? = null,
    var avatarUrl: String? = null,
    var userId: String? = null,
    var token: String? = null,
    var jaToken: String? = null,
    var isNewUser: Boolean = false,
) : Serializable