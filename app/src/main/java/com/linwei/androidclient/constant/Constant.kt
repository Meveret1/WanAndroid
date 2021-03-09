package com.linwei.androidclient.constant


import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.gson.Gson
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.retrofit.RetrofitService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*

object Constant {
    const val CONTENT_URL_KEY = "url"
    const val CONTENT_TITLE_KEY = "title"
    const val CONTENT_ID_KEY = "id"
    const val CONTENT_BODY_KEY = "body"
    const val BASE_URL = "https://www.wanandroid.com"

    lateinit var retrofithelper: RetrofitService

    var isNetWrokable: Boolean = true
    var isload: Boolean = false

    val client = OkHttpClient()

    var oldcookie = ""

    fun getchenjin(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE )
        //or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = Color.TRANSPARENT
    }



}