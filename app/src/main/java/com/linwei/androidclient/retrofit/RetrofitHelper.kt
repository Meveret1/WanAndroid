package com.linwei.androidclient.retrofit

import com.linwei.androidclient.constant.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private val retrofit= Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(serviceClass:Class<T>):T{
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> create():T {
        return create(T::class.java)
    }
}