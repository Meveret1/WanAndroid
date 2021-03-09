package com.linwei.androidclient

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.retrofit.RetrofitHelper
import com.linwei.androidclient.retrofit.RetrofitService

class MyApplication : Application() {
    companion object {
        lateinit var context: Context
    }

    private lateinit var sp: SharedPreferences
    private var newcookie: String? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sp = getSharedPreferences("data", Context.MODE_PRIVATE)
        newcookie = sp.getString("cookie", "")
        if (newcookie != "") {
            Constant.oldcookie = newcookie!!
            Constant.isload = true
        }
        Constant.retrofithelper = RetrofitHelper.create<RetrofitService>()//创建retrofit
    }


}