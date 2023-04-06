package com.example.myapp.base.bridge.home.entity

data class BannerEntity(
    // 图片展示地址
    var imgUrl: String? = null,

    // h5跳转链接 {com.example.myapp.base.bridge.utils.H5JumpUtil.jumpByUri(android.net.Uri)}
    var link: String? = null,

    // 标题
    var title: String? = null,

    // id
    var id: String? = null,
)