package com.linwei.androidclient.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel : ViewModel() {
    private val tAg = "Welcome"
    val newcookie: LiveData<String>
        get() = _newcookie
    val jsonData: LiveData<JsonData>
        get() = _jsonData

    private val _newcookie = MutableLiveData<String>()
    private val _jsonData = MutableLiveData<JsonData>()

    fun requestnetwork(username: String, password: String) {
        Constant.retrofithelper.loginSystem(username, password)
            .enqueue(object : Callback<JsonData> {
                override fun onResponse(call: Call<JsonData>, response: Response<JsonData>) {
                    val cookie = response.headers().values("Set-Cookie")
                    _newcookie.value = cookie.toString()
                    Log.e(tAg, cookie.toString())
                    val bean = response.body()!!
                    _jsonData.value = bean
                }

                override fun onFailure(call: Call<JsonData>, t: Throwable) {
                    Log.e(tAg, t.toString())
                }

            })
    }
}