package com.linwei.androidclient.bean

data class CollectData(
    val `data`: CollectData1,
    val errorCode: Int,
    val errorMsg: String
)

data class CollectData1(
    val curPage: Int,
    val datas: MutableList<CollectData2>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class CollectData2(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val title: String,
    val userId: Int,
    val visible: Int,
    val zan: Int
)