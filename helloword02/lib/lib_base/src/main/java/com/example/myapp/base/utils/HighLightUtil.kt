package com.example.myapp.base.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

class HighLightUtil {

    companion object {
        /**
         *  关键字高亮显示
         *  text  原文
         *  keyWord 需要高亮显示的关键字
         *  isCut 是否需要做分词高亮展示
         *  isCut = true  关键字里的每一个字，只要有都会高亮
         *  isCut = false（默认） 只有整词才会高亮
         **/
        @JvmStatic
        fun stringToHighLight(
            text: String,
            keyWord: String,
            isCut: Boolean = false
        ): SpannableStringBuilder {
            val spannable = SpannableStringBuilder(text)
            try {
                var keyword: MutableList<String> = ArrayList()
                if (isCut) {
                    for (i in keyWord.indices) {
                        keyword.add(keyWord.substring(i, i + 1))
                    }
                } else {
                    keyword = arrayListOf(keyWord)
                }
                var span: CharacterStyle?
                var wordReg: String
                for (i in keyword.indices) {
                    var key = ""
                    if (keyword[i].contains("*") || keyword[i].contains("(") || keyword[i].contains(
                            ")"
                        )
                    ) {
                        val chars = keyword[i].toCharArray()
                        for (k in chars.indices) {
                            key = if (chars[k] == '*' || chars[k] == '(' || chars[k] == ')') {
                                key + "\\" + chars[k].toString()
                            } else {
                                key + chars[k].toString()
                            }
                        }
                        keyword[i] = key
                    }
                    wordReg = "(?i)" + keyword[i]
                    val pattern: Pattern = Pattern.compile(wordReg)
                    val matcher: Matcher = pattern.matcher(text)
                    while (matcher.find()) {
                        span = ForegroundColorSpan(Color.parseColor("#0099F2"))
                        spannable.setSpan(
                            span,
                            matcher.start(),
                            matcher.end(),
                            Spannable.SPAN_MARK_MARK
                        )
                    }
                }
            } catch (e: Exception) {
                LogUtils.d("stringToHighLight-Error-------->$e")
            }
            return spannable
        }
    }
}