package com.linwei.androidclient.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.linwei.androidclient.R
import com.linwei.androidclient.adapter.PageFragmentAdapter
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.fragment.FragmentInterface
import com.linwei.androidclient.fragment.LoginFragment
import com.linwei.androidclient.fragment.RegisterFragment
import kotlinx.android.synthetic.main.activity_login.*


class RegisterActivity : AppCompatActivity() {
    private val mfragment = ArrayList<Fragment>()
    private lateinit var pageFragmentAdapter: PageFragmentAdapter

    private lateinit var fragmentInterface:FragmentInterface


    fun setFragmentInterface(fragmentInterface:FragmentInterface){
        this.fragmentInterface=fragmentInterface
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constant.getchenjin(this)
        setContentView(R.layout.activity_login)
        initView()
    }

    fun initView(){
        val frag1=LoginFragment()
        val frag2=RegisterFragment()
        mfragment.add(frag1)
        mfragment.add(frag2)
        pageFragmentAdapter=PageFragmentAdapter(
            supportFragmentManager,mfragment, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        loginViewPager.adapter=pageFragmentAdapter

    }
    fun jump(){
        fragmentInterface.jumpFragment(loginViewPager)
    }

}