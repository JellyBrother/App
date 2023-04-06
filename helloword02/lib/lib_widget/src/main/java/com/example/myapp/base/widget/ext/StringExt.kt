package com.example.myapp.base.widget.ext

import android.text.Html
import android.text.SpannableStringBuilder

fun String.highlight(keywords: String, color: String): CharSequence {
    if (keywords.isEmpty()) {
        return this
    }
    val htmlText = Regex(keywords).replace(this) { matchText ->
        String.format(
            "<font color='%s'>${matchText.value}</font>",
            color
        )
    }
    return SpannableStringBuilder(Html.fromHtml(htmlText))
}

fun String.highlightBlue(keywords: String): CharSequence {
    return highlight(keywords, "#0099F2")
}