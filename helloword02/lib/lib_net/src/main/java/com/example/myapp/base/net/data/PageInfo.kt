package com.example.myapp.base.net.data

data class PageInfo<T>(
    val pageNum: Int,
    val pageSize: Int,
    val isFirstPage: Boolean,
    val hasNextPage: Boolean,
    val list: List<T> = emptyList(),
    // 提供额外字段
    val info: Any? = null
)