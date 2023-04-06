package com.example.myapp.base.net.data

open class JaPageReq {
    var pageNum: Int = 0
    var pageSize: Int = 0

    constructor()

    constructor(num: Int, size: Int) {
        pageNum = num
        pageSize = size
    }

    companion object {
        val NoPage = JaPageReq(1, 10000)
    }

    fun isRefresh(): Boolean {
        return pageNum == 1
    }
}
