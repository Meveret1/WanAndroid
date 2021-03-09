package com.linwei.androidclient.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.R
import com.linwei.androidclient.activity.MainActivity
import com.linwei.androidclient.activity.RegisterActivity
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.model.LoginModel
import kotlinx.android.synthetic.main.actvity_log_in.*

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var username: String
    private lateinit var password: String
    private lateinit var newcookie: String
    private lateinit var sp: SharedPreferences
    private lateinit var loginModel: LoginModel

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.actvity_log_in, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sp = activity!!.getSharedPreferences("data", Context.MODE_PRIVATE)
        loginModel = ViewModelProvider.AndroidViewModelFactory(activity!!.application)
            .create(LoginModel::class.java)
        loginBtn.setOnClickListener(this)
        loginGoReg.setOnClickListener(this)

        loginModel.newcookie.observe(activity!!, Observer{ it ->
            newcookie = it.toString()
        })

        loginModel.jsonData.observe(activity!!,Observer { t ->

            when (t.errorCode) {
                -1 -> Toast.makeText(MyApplication.context, t.errorMsg, Toast.LENGTH_SHORT)
                    .show()
                0 -> {
                    Toast.makeText(MyApplication.context, "登录成功", Toast.LENGTH_SHORT)
                        .show()
                    sp.edit {
                        putString("username", username)
                        putString("password", password)
                        putString("cookie", newcookie)
                    }
                    Constant.isload = true
                    Constant.oldcookie = newcookie
                    loginBtn.text = "注销登录"
                    gohome()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val userword = sp.getString("username", "")
        val passname = sp.getString("password", "")
        if (userword != "") loginUsername.setText(userword)
        if (passname != "") loginPasswrod.setText(passname)
    }

    override fun onClick(v: View) {
        when (v.id) {
            loginBtn.id -> {
                editisnull()
            }
            loginGoReg.id -> {
                //跳转到下一个fragment
                jumpToRegister()
            }
        }
    }

    private fun editisnull() {
        val user = loginUsername.text.toString().trim()
        val pass = loginPasswrod.text.toString().trim()
        Log.e("Login", "$user ***  $pass")
        if (user == "" || pass == "") {
            Log.e("Login", "$user ***  $pass")
            Snackbar.make(view!!, "有空值", Snackbar.LENGTH_SHORT).show()
        } else {
            username = user
            password = pass
            loginModel.requestnetwork(username, password)
        }

    }

    private fun jumpToRegister() {
        val registerActivity = activity as RegisterActivity
        registerActivity.setFragmentInterface(object : FragmentInterface {
            override fun jumpFragment(viewpager: ViewPager) {
                viewpager.currentItem = 1
            }
        })
        registerActivity.jump()
    }

    private fun gohome() {
        val intent = Intent(activity, MainActivity::class.java)
        activity!!.startActivity(intent)
        activity!!.finish()
    }

}