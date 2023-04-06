package com.example.myapp.base.widget.multistate

import androidx.annotation.IntDef

@IntDef(
    LayoutStateType.STATE_CONTENT,
    LayoutStateType.STATE_LOADING,
    LayoutStateType.STATE_EMPTY,
    LayoutStateType.STATE_ERROR,
    LayoutStateType.STATE_NO_NETWORK
)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutStateType {
    companion object {
        const val STATE_CONTENT = 0
        const val STATE_LOADING = 1
        const val STATE_EMPTY = 2
        const val STATE_ERROR = 3
        const val STATE_NO_NETWORK = 4
    }
}
