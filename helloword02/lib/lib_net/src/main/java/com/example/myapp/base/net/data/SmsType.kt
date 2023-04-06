package com.example.myapp.base.net.data

import androidx.annotation.IntDef

@IntDef(
    SmsType.REGISTER_LOGIN,
    SmsType.CHANGE_PHONE_OLD,
    SmsType.CHANGE_PHONE_NEW
)
@Retention(AnnotationRetention.SOURCE)
annotation class SmsType {
    companion object {
        const val REGISTER_LOGIN = 0
        const val CHANGE_PHONE_OLD = 1
        const val CHANGE_PHONE_NEW = 2
    }
}
