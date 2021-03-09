package com.linwei.androidclient.bean

data class BannerData(
    val `data`: List<Bannerlist>,
    val errorCode: Int,
    val errorMsg: String
)

data class Bannerlist(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)