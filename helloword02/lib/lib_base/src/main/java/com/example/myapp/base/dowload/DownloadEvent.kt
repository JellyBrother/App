package com.example.myapp.base.dowload

import android.os.Parcel
import android.os.Parcelable

data class DownloadEvent(
    // 下载状态
    @DownloadStatus var status: Int = DownloadStatus.ON_START,
    // 下载地址
    var url: String? = "",
    // 文件名称
    var fileName: String? = "",
    // 文件总大小
    var totalSize: Long = 0,
    // 文件当前下载大小
    var currentSize: Long = 0,
    var resourceId: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(status)
        parcel.writeString(url)
        parcel.writeString(fileName)
        parcel.writeLong(totalSize)
        parcel.writeLong(currentSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownloadEvent> {
        override fun createFromParcel(parcel: Parcel): DownloadEvent {
            return DownloadEvent(parcel)
        }

        override fun newArray(size: Int): Array<DownloadEvent?> {
            return arrayOfNulls(size)
        }
    }
}
