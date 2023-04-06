package com.example.myapp.base.net.data.mapper

class DefaultMapper<I, O> : Mapper<I, O> {
    override fun map(input: I): O {
        return input as O
    }
}