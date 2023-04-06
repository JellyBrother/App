package com.example.myapp.base.widget.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.myapp.base.cache.FileUtils
import com.example.myapp.base.cache.SDCardUtils
import com.example.myapp.base.constant.BaseConstant
import com.example.myapp.base.utils.LogUtils
import java.io.File
import java.util.*

object ImageUriUtils {
    private val TAG = ImageUriUtils.javaClass.simpleName
    private var IMAGE_UNIQUE_KEY = UUID.randomUUID().toString()

    private fun getImageFile(context: Context, name: String): File {
        return File(
            getAppImageDir(context), "$IMAGE_UNIQUE_KEY$name.jpg"
        )
    }

    fun getTempImageSrcFile(context: Context): File {
        return getImageFile(context, "CROP.jpg")
    }

    fun getRealImageFile(context: Context): File {
        return getImageFile(context, "")
    }

    fun getTempImageCompatibleSrcFile(context: Context): File {
        return getImageFile(context, "COMPATIBLE_CROP")
    }

    fun getAppImageDir(cxt: Context): String? {
        val dir = if (SDCardUtils.isSDCardEnableByEnvironment()) {
            File(FileUtils.getAppRootPath(), BaseConstant.Path.IMAGE + File.separator)
        } else {
            cxt.filesDir
        }
        if (!dir!!.exists()) {
            dir.mkdirs()
        }
        return dir.path
    }

    fun queryUriByPath(mContext: Context, filePath: String): Uri? {
        var uri: Uri? = null
        val where = MediaStore.Images.Media.DATA + "='" + filePath + "'"
        val cursor = mContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, where, null, null
        )
        if (cursor?.count != 1) {
            LogUtils.e(TAG, "more than one, so delete all data $filePath")
            deleteUriByFilePath(mContext, filePath)
            return null
        }
        if (cursor.moveToFirst()) {
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            LogUtils.i(TAG, "generateUri: $data, $id")
        } else {
            LogUtils.i(TAG, "not found  $filePath")
        }
        cursor.close()
        return uri
    }

    /**
     * 删除uri会顺带把文件删除掉
     */
    fun deleteUriByFilePath(mContext: Context, filePath: String) {
        val where = MediaStore.Images.Media.DATA + "='" + filePath + "'"
        mContext.contentResolver.delete(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            where, null
        )
    }

    fun updateUri(mContext: Context, uri: Uri, title: String, content: String) {
        val contentValues = ContentValues(1)
        contentValues.put(MediaStore.Images.Media.TITLE, title)
        contentValues.put(MediaStore.Images.Media.DATA, content)
        mContext.contentResolver.update(
            uri, contentValues,
            null, null
        )
    }

    fun generateUri(mContext: Context, title: String, content: String?): Uri? {
        val contentValues = ContentValues(1)
        contentValues.put(MediaStore.Images.Media.TITLE, title)
        contentValues.put(MediaStore.Images.Media.DATA, content)
        return mContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )
    }

    fun file2Uri(mContext: Context, file: File): Uri {
        val authority: String =
            mContext.applicationContext.packageName.toString() + ".imageCrop.fileProvider"
        return FileProvider.getUriForFile(mContext, authority, file)
    }

    fun getImageUniqueKey(): String {
        return IMAGE_UNIQUE_KEY
    }
}