package com.example.myapp.base.widget.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View

fun Context.inflateView(layoutId: Int): View =
    LayoutInflater.from(this).inflate(layoutId, null)