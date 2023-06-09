package com.jelly.myapp.module.main.ui.activity

import android.view.View
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import com.jelly.myapp.base.ui.BaseActivity

class LocationActivity : BaseActivity() {

    override fun getLayoutView(): View {
        return ComposeView(this).apply {
            setContent {
                Text("Hello world!")
            }
        }
    }
}