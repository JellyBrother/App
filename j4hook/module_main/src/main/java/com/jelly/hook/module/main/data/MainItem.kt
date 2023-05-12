package com.jelly.hook.module.main.data

import android.view.View

data class MainItem(
    var text: String,
    var onClickListener: View.OnClickListener,
)