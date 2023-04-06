package com.example.myapp.base.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.Boolean

class NullStringTypeAdapter : TypeAdapter<String>() {
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): String {
        return when (`in`.peek()) {
            JsonToken.STRING, JsonToken.NUMBER -> `in`.nextString()
            JsonToken.BOOLEAN -> Boolean.toString(`in`.nextBoolean())
            JsonToken.NULL -> {
                `in`.nextNull()
                ""
            }
            else -> {
                `in`.skipValue()
                throw IllegalArgumentException()
            }
        }
    }

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: String?) {
        out.value(value)
    }
}