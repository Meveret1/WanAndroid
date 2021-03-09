package com.linwei.androidclient.model

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectModel : ViewModel() {

    val jsonData: LiveData<JsonData>
        get() = _jsonData
    private val _jsonData = MutableLiveData<JsonData>()

    fun getProjectList(pages: Int) {
        Constant.run {
            retrofithelper.getHotProject(oldcookie, pages)
                .enqueue(object : Callback<JsonData> {
                    override fun onResponse(call: Call<JsonData>, response: Response<JsonData>) {
                        _jsonData.value=response.body()
                    }
                    override fun onFailure(call: Call<JsonData>, t: Throwable) {
                        Toast.makeText(MyApplication.context,t.message,Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


}