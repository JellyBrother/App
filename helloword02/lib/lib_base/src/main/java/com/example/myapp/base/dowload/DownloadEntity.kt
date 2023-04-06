package com.example.myapp.base.dowload

data class DownloadEntity(
    val version: String,
    @DownloadStatus val downloadStatus: Int,
)