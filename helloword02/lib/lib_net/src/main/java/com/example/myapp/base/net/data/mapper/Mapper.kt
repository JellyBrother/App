package com.example.myapp.base.net.data.mapper

interface Mapper<I, O> {
    fun map(input: I): O
}