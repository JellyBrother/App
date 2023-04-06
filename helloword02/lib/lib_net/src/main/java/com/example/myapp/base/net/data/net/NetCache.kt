package com.example.myapp.base.net.net

interface NetCache<T> {
    fun hasCache(): Boolean
    fun getCache(): T
    fun putCache(t: T)
    fun clearCache()
}