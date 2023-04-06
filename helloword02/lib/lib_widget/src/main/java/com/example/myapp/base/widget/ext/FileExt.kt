package com.example.myapp.base.widget.ext

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultiPartBody(partName: String) =
    run {
        val requestBody = this.asRequestBody("application/otcet-stream".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, this.name, requestBody)
    }