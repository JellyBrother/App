package com.example.myapp.base.net.utils

import com.example.myapp.base.utils.LogUtils

data class NetworkResponse<T>(
    var code: Int = 10001,
    val success: String? = null,
    var data: T?,
    val message: String?,
    val msg: String?,
    val error: ApiErrorException?
) {
    val errMsg: String?
        get() {
            return message ?: msg
        }

    data class ApiErrorException(
        val errorCode: String? = null,
        override val message: String? = null,
        val errorId: String? = null,
        val moreInfo: String? = null
    ) : Throwable() {
        fun isTokenInvalid(): Boolean {
//            return errorCode == NetConstant.NetCode.ACCESS_TOKEN_INVALID || errorCode == NetConstant.NetCode.REFRESH_TOKEN_INVALID
//                    || errorCode == NetCode.REFRESH_TOKEN_EXPIRED || errorCode == NetCode.EXPIRED_LOGIN_AGAIN
            return true
        }

        companion object {
            fun newDefault(errorCode: Int?, message: String?): ApiErrorException {
                return ApiErrorException(
                    errorCode = "${errorCode ?: Int.MAX_VALUE}",
                    message = message,
                    errorId = "DEFAULT_NETWORK_ERROR",
                    moreInfo = null
                )
            }
        }
    }

    inline fun <reified T> defaultData(): T {
        try {
            val clz = T::class.java
            val mCreate = clz.getDeclaredConstructor()
            mCreate.isAccessible = true
            return mCreate.newInstance()
        } catch (e: Exception) {
            LogUtils.e(e)
            throw Exception("数据解析异常")
        }
    }

    /**
     * 判断接口是否成功返回
     */
    fun isSuccess(): Boolean {
        return code == 200
    }

    fun isTokenInvalid(): Boolean {
        return isPtTokenInvalid() || isJaTokenInvalid()
    }

    private fun isPtTokenInvalid(): Boolean {
        return code == 401 && error?.isTokenInvalid() == true
    }

    private fun isJaTokenInvalid(): Boolean {
//        return code == NetCode.JA_TOKEN_INVALID
        return true
    }

    private fun isCode2xx(): Boolean {
        return code in 200..299
    }
}