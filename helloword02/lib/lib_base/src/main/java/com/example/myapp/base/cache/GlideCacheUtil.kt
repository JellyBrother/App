package com.example.myapp.base.cache

import android.os.Looper
import androidx.annotation.MainThread
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCache
import com.example.myapp.base.utils.Utils
import java.io.File

/**
 * Glide缓存工具类
 */
object GlideCacheUtil {
    private val glideInternalCachePath =
        File(Utils.getApp().cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
    private val glideExternalCachePath =
        File(Utils.getApp().externalCacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)

    /**
     * 清除图片磁盘缓存
     */
    private fun clearImageDiskCache() {
        try {
            //只能在主线程执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(Utils.getApp()).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片内存缓存
     */
    private fun clearImageMemoryCache() {
        try {
            //只能在主线程执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(Utils.getApp()).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片所有缓存
     */
    @MainThread
    fun clearImageAllCache() {
        clearImageDiskCache()
        clearImageMemoryCache()
        FileUtils.deleteAllInDir(glideInternalCachePath)
        FileUtils.deleteAllInDir(glideExternalCachePath)
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    fun getCacheLength(): Long {
        try {
            return FileUtils.getLength(glideInternalCachePath) + FileUtils.getLength(
                glideExternalCachePath
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}