package com.example.myapp.base.net.data.local.dao

import androidx.room.*
import com.example.myapp.base.net.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(userEntity: UserEntity)

    @Query("SELECT * FROM user_entity where userId = :userId LIMIT 1")
    fun getByUserId(userId: String): UserEntity?

    @Query("UPDATE user_entity SET mobile = :mobile where userId = :userId")
    fun updateMobile(mobile: String, userId: String)

    @Query("UPDATE user_entity SET nickName = :nickName where userId = :userId")
    fun updateNickName(nickName: String, userId: String)

    @Query("UPDATE user_entity SET avatarUrl = :avatarUrl where userId = :userId")
    fun updateAvatar(avatarUrl: String, userId: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(userEntity: UserEntity): Int

    @Query("DELETE FROM user_entity")
    fun deleteAll()
}