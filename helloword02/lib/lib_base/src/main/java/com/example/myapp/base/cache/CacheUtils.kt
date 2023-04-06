package com.example.myapp.base.cache

import com.example.myapp.base.utils.ConvertUtils
import com.example.myapp.base.utils.SPUtils
import com.example.myapp.base.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object CacheUtils {
    fun clearCache(cacheSize: String): Flow<String> {
        return flow {
            delay(500)
            clean()
            emit(cacheSize)
        }
    }

    fun clean() {
        // Glide cache
        GlideCacheUtil.clearImageAllCache()
        // share cache
        CleanUtils.cleanShareCache()
        // http cache
        CleanUtils.cleanHttpCache()

        val files = Utils.getApp().fileList()
        for (i in files.indices) {
            if (files[i].contains(".jpg") || files[i].contains(".apk")) Utils.getApp()
                .deleteFile(files[i])
        }
    }

    fun cleanAll() {
        clean()
        // sp缓存
        SPUtils.getInstance("file_download_id").clear()
        SPUtils.getInstance("file_common").clear()
        CleanUtils.cleanCacheDir()
        CleanUtils.cleanUseFileDir()
    }

    fun getCacheSize(): String {
        // Glide cache
        val glideCacheSize = GlideCacheUtil.getCacheLength()
        // share cache
        val shareSize = FileUtils.getLength(CachePath.getShareDir())
        // http cache
        val httpSize = FileUtils.getLength(CachePath.getHttpCacheDir())

        return ConvertUtils.byte2FitMemorySize(glideCacheSize + shareSize + httpSize, 2)
    }
}