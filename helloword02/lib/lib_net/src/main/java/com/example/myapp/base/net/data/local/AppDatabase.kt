package com.example.myapp.base.net.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapp.base.net.data.local.dao.UserDao
import com.example.myapp.base.net.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}