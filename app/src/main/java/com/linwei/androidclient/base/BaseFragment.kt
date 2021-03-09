package com.linwei.androidclient.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    private var isOk = false
    private var isFirst = true
    lateinit var vieww: View
    val mHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vieww = getMyView(inflater, container, savedInstanceState)
        initView()
        isOk = true
        return vieww
    }

    // 子fragment初始化view的方法
    abstract fun initView()

    // 获取子fragment的view
    abstract fun getMyView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    override fun onResume() {
        super.onResume()
        initLoadData()
    }

    private fun initLoadData() {
        if (isOk && isFirst) { // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据
            loadData()
            isFirst = false // 加载第一次数据后改变状态，后续不再重复加载
        }
    }

    // 子fragment实现懒加载的方法
    abstract fun loadData()

}