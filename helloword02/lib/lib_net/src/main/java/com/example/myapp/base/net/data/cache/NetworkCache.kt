package com.example.myapp.base.net.data.cache

interface NetworkCache<T> {
    suspend fun get(): T?
    suspend fun put(t: T)
    suspend fun clear(): Boolean
}