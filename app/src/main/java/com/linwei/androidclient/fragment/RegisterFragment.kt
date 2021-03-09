package com.linwei.androidclient.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.R
import com.linwei.androidclient.activity.RegisterActivity
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.actvity_register.*
import okhttp3.FormBody
import okhttp3.Request

class RegisterFragment : Fragment() ,View.OnClickListener{

    private lateinit var username: String
    private lateinit var password: String
    private lateinit var password2: String
    private lateinit var sp: SharedPreferences
    private lateinit var requestBody: FormBody
    private lateinit var newcookie: String

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.actvity_register, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sp = activity!!.getSharedPreferences("data", Context.MODE_PRIVATE)
        regBtn.setOnClickListener(this)
        regGoLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            regBtn.id->{
                editisnull()
            }
            regGoLogin.id->{
                jumpToLogin()
            }

        }
    }

    private fun editisnull() {
        val user = regUsername.text.toString().trim()
        val pass = regPasswrod.text.toString().trim()
        val pass2 = regPasswrod2.text.toString().trim()

        if (user == "" || pass == "" || pass2 == "") {
            Snackbar.make(view!!, "有空值", Snackbar.LENGTH_SHORT).show()
        } else {
            username = user
            password = pass
            password2 = pass2
            requestBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("repassword",password2)
                .build()
            registerRequest()
        }

    }

    private fun registerRequest() {
        val observable = Observable.create(ObservableOnSubscribe<JsonData> { emitter ->
            //editisnull()
            val request = Request.Builder()
                .url("https://www.wanandroid.com/user/register")
                .post(requestBody)
                .build()
            val response = Constant.client.newCall(request).execute()
            val header = response.headers
            val cookies = header.values("Set-Cookie")
            newcookie = cookies.toString()
            val responesstr = response.body!!.string()
            if (responesstr != "") {
                val gson = Gson()
                val jsonData = gson.fromJson(responesstr, JsonData::class.java)
                emitter?.onNext(jsonData)
            }
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
        //观察者
        val observer = object : Observer<JsonData> {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: JsonData) {
                when (t.errorCode) {
                    -1 -> Toast.makeText(MyApplication.context, t.errorMsg, Toast.LENGTH_SHORT)
                        .show()
                    0 -> {
                        Toast.makeText(MyApplication.context, "注册成功", Toast.LENGTH_SHORT)
                            .show()
                        sp.edit {
                            putString("username", username)
                        }
                        jumpToLogin()
                    }
                }
            }

            override fun onComplete() {

            }

            override fun onError(e: Throwable?) {
                Log.e("Error", e.toString())
            }

        }
        observable.subscribe(observer)


    }
    fun jumpToLogin(){
        val registerActivity=activity as RegisterActivity
        registerActivity.setFragmentInterface(object :FragmentInterface{
            override fun jumpFragment(viewpager: ViewPager) {
                viewpager.currentItem=0
            }
        })
        registerActivity.jump()
    }

//    fun gohome() {
//        val intent = Intent(activity, MainActivity::class.java)
//        activity!!.startActivity(intent)
//        activity!!.finish()
//    }


}