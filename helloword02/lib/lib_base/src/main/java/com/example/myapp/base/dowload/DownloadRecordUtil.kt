package com.example.myapp.base.dowload

import com.example.myapp.base.cache.FileUtils
import com.example.myapp.base.utils.GsonUtils
import com.example.myapp.base.utils.LogUtils
import com.example.myapp.base.utils.SPUtils

object DownloadRecordUtil {
    // 下载文件
    val FILE_DOWNLOAD_ID = "file_download_id"

    fun generateDownloadRecord(
        fileName: String
    ) {
        val localVersion = getDownloadVersion(fileName)
        SPUtils.getInstance(FILE_DOWNLOAD_ID).put(
            fileName, GsonUtils.toJson(
                DownloadEntity(
                    version = localVersion,
                    downloadStatus = DownloadStatus.ON_NOT_DOWNLOAD
                )
            )
        )
    }

    fun clearDownloadRecord(fileName: String) {
        SPUtils.getInstance(FILE_DOWNLOAD_ID).remove(fileName)
    }

    fun recordDownloadIconType(
        fileName: String,
        @DownloadStatus downloadStatus: Int
    ) {
        val downloadEntity = getDownloadEntity(fileName) ?: return
        SPUtils.getInstance(FILE_DOWNLOAD_ID).put(
            fileName, GsonUtils.toJson(
                downloadEntity.copy(
                    downloadStatus = downloadStatus
                )
            )
        )
    }

    /**
     * 下载成功更新资料版本号
     */
    fun updateFileVersion(fileName: String, version: String) {
        val downloadEntity = getDownloadEntity(fileName) ?: return
        SPUtils.getInstance(FILE_DOWNLOAD_ID).put(
            fileName, GsonUtils.toJson(
                downloadEntity.copy(
                    version = version
                )
            )
        )
    }

    fun getDownloadEntity(fileName: String): DownloadEntity? {
        return try {
            GsonUtils.fromJson(
                SPUtils.getInstance(FILE_DOWNLOAD_ID).getString(fileName),
                DownloadEntity::class.java
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getDownloadStatus(fileName: String): Int {
        if (!isFileDownload(fileName)) {
            clearDownloadRecord(fileName)
            return DownloadStatus.ON_NOT_DOWNLOAD
        }

        return getDownloadEntity(fileName)?.downloadStatus
            ?: DownloadStatus.ON_NOT_DOWNLOAD
    }

    fun getDownloadSize(fileName: String): Long {
        if (!isFileDownload(fileName)) {
            clearDownloadRecord(fileName)
            return 0
        }

        return FileUtils.getLength(DownloadUtil.getDownloadFile(fileName))
    }

    private fun getDownloadVersion(fileName: String): String {
        val version = getDownloadEntity(fileName)?.version ?: ""
        LogUtils.d("当前资料${fileName}本地版本号为${version}")
        return version
    }

    fun needUpdate(fileName: String, remoteVersion: String): Boolean {
        val localVersion = getDownloadVersion(fileName)
        return localVersion.isNotEmpty() && localVersion != remoteVersion
    }

    private fun isFileDownload(fileName: String): Boolean {
        return FileUtils.isFileExists(DownloadUtil.getDownloadFile(fileName))
    }
}