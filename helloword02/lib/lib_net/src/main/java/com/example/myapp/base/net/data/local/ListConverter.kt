package com.example.myapp.base.net.data.local

import androidx.room.TypeConverter
import com.example.myapp.base.utils.GsonUtils
import com.google.gson.reflect.TypeToken

open class ListConverter<T> {
    @TypeConverter
    fun objectToString(list: List<T>): String {
        return GsonUtils.toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String): List<T> {
        val listType = object : TypeToken<List<T>>() {}.type
        return GsonUtils.fromJson(json, listType)
    }
}