package com.inovance.palmhouse.base.bridge.helper

import androidx.room.Room
import com.example.myapp.base.net.data.local.AppDatabase
import com.example.myapp.base.net.data.local.entity.UserEntity
import com.example.myapp.base.utils.Utils

/**
 * 保证数据库中只存在一条用户数据
 */
class DatabaseHelper {

    private var database: AppDatabase = Room.databaseBuilder(
        Utils.getApp(),
        AppDatabase::class.java, "palm-house"
    ).build()

    suspend fun addUser(user: UserEntity) {
        database.userDao().insert(user)
    }

    fun deleteAllUser() {
        database.userDao().deleteAll()
    }

    companion object {
        val INSTANCE: DatabaseHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DatabaseHelper()
        }
    }
}