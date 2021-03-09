package com.linwei.androidclient.model

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.bean.CollectData
import com.linwei.androidclient.constant.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectModel:ViewModel() {
    val collectData: LiveData<CollectData>
        get() = _collectData
    private val _collectData = MutableLiveData<CollectData>()
    fun getcollectList(pages: Int){

        Constant.run {
            retrofithelper.getCollectArticle(oldcookie,pages).enqueue(object:Callback<CollectData>{
                override fun onResponse(call: Call<CollectData>, response: Response<CollectData>) {
                    _collectData.value=response.body()
                }

                override fun onFailure(call: Call<CollectData>, t: Throwable) {
                    Toast.makeText(MyApplication.context,t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}