package com.example.myapp.base.dowload

import androidx.annotation.IntDef

@IntDef(
    DownloadStatus.ON_NOT_DOWNLOAD,
    DownloadStatus.ON_START,
    DownloadStatus.ON_PROGRESS,
    DownloadStatus.ON_PAUSE,
    DownloadStatus.ON_COMPLETE,
    DownloadStatus.ON_CANCEL
)
@Retention(AnnotationRetention.SOURCE)
annotation class DownloadStatus {
    companion object {
        // 未下载
        const val ON_NOT_DOWNLOAD = 0

        // 开始下载
        const val ON_START = 1

        // 下载中
        const val ON_PROGRESS = 2

        // 暂停
        const val ON_PAUSE = 3

        // 下载完成
        const val ON_COMPLETE = 4

        // 取消下载
        const val ON_CANCEL = 5
    }
}