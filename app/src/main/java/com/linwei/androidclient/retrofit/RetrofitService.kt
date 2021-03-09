package com.linwei.androidclient.retrofit

import com.linwei.androidclient.bean.BannerData
import com.linwei.androidclient.bean.CollectData
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.bean.TreeData
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    /**
     *登录
     */
    @POST("/user/login")
    @FormUrlEncoded
    fun loginSystem(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<JsonData>

    /**
     * 注册
     */
    @POST("/user/register")
    @FormUrlEncoded
    fun registerSystem(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassowrd: String
    ): Call<JsonData>


    /**
     * 收藏文章
     */
    @POST("/lg/collect/{id}/json")
    fun addCollect(@Header("Cookie") cookie: String, @Path("id") id: Int): Call<JsonData>

    /**
     * 移除收藏的文章
     */

    @POST("/lg/uncollect_originId/{id}/json")
    fun removeCollect(
        @Header("Cookie") cookie: String,
        @Path("id") id: Int
    ): Call<JsonData>

    /**
     * 获取已经收藏的文章列表
     */

    @GET("lg/collect/list/{pages}/json")
    fun getCollectArticle(
        @Header("Cookie") cookie: String,
        @Path("pages") pages: Int
    ): Call<CollectData>

    /**
     *首页下面的文章
     */
    @GET("/article/list/{pages}/json")
    fun getHotArticle(@Header("Cookie") cookie: String, @Path("pages") pages: Int): Call<JsonData>

    /**
     * 热门项目
     */
    @GET("/article/listproject/{pages}/json")
    fun getHotProject(@Header("Cookie") cookie: String, @Path("pages") pages: Int): Call<JsonData>

    /**
     * 获取知识体系列表
     */
    @GET("/tree/json")
    fun getTreeList(): Call<TreeData>

    /**
     * 获取体系下的文章
     */
    @GET("/article/list/{pages}/json")
    fun getTreeArticle(
        @Header("Cookie") cookie: String,
        @Path("page") pages: Int,
        @Query("cid") cid: Int
    ): Call<JsonData>

    /**
     * 获取首页Banner
     */
    @GET("/banner/json")
    fun getBanner():Call<BannerData>

}