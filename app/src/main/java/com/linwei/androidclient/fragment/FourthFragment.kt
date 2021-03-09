package com.linwei.androidclient.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.linwei.androidclient.R
import com.linwei.androidclient.activity.TreeActivity
import com.linwei.androidclient.adapter.OnItemClickListener
import com.linwei.androidclient.adapter.TreeAdapter
import com.linwei.androidclient.base.BaseFragment
import com.linwei.androidclient.bean.Children
import com.linwei.androidclient.bean.Data1
import com.linwei.androidclient.bean.TreeData
import com.linwei.androidclient.constant.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request

class FourthFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressbar4: ProgressBar
    private lateinit var treeAdapter: TreeAdapter

    private lateinit var bodyStr:String
    private lateinit var oneStep: List<Data1>


    private val tag1 = "FourthFragment"


    override fun initView() {
        recyclerView = vieww.findViewById(R.id.tressRecycler)
        refreshLayout = vieww.findViewById(R.id.refreshList4)
        progressbar4 = vieww.findViewById(R.id.progressBar4)
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        initRefreshLayout()
    }

    private fun initRefreshLayout() {
        refreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light, android.R.color.holo_red_light,
            android.R.color.holo_orange_light, android.R.color.holo_green_light
        )
        refreshLayout.setOnRefreshListener(this)

    }

    override fun getMyView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater!!.inflate(R.layout.fourth_activity, container, false)
    }

    override fun loadData() {
        requestData()
    }


    private fun requestData() {



        val observableOnSubscribe = Observable.create<TreeData> {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.wanandroid.com/tree/json")
                .header("Cookie",Constant.oldcookie)
                .build()
            val respones = client.newCall(request).execute()
            bodyStr = respones.body!!.string()
            if (bodyStr != "") {
                val gson = Gson()
                val treedata = gson.fromJson(bodyStr, TreeData::class.java)
                it.onNext(treedata)
            }
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

        val observer = object : Observer<TreeData> {
            override fun onComplete() {
                //Log.e(tag1, "请求完成")
            }

            override fun onSubscribe(d: Disposable?) {
                //Log.e(tag1, "订阅成功")
            }

            override fun onNext(t: TreeData) {
                    oneStep = t.data
                    initRecycle()


            }

            override fun onError(e: Throwable?) {
                Log.e(tag1, "请求失败")
            }

        }
        observableOnSubscribe.subscribe(observer)

    }

    fun initRecycle() {
        showProgress()
        treeAdapter = TreeAdapter(oneStep)
        recyclerView.adapter = treeAdapter
        treeAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(pos: Int, title: String, url: String) {

            }

            override fun onClick(pos: Int) {
                val intent=Intent(activity,TreeActivity::class.java)
                intent.putExtra(Constant.CONTENT_BODY_KEY,bodyStr)
                intent.putExtra(Constant.CONTENT_ID_KEY,pos)
                activity?.startActivity(intent)
            }

        })

    }
    private fun showProgress() {
        if (progressbar4.visibility == View.VISIBLE) progressbar4.visibility = View.GONE
        else progressbar4.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = true


        mHandler.postDelayed({
            refreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

}