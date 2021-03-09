package com.linwei.androidclient.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.linwei.androidclient.R
import com.linwei.androidclient.activity.WebActivity
import com.linwei.androidclient.adapter.HomeListAdapter
import com.linwei.androidclient.adapter.OnItemClickListener
import com.linwei.androidclient.base.BaseFragment
import com.linwei.androidclient.bean.Data
import com.linwei.androidclient.bean.DataX
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.first_activity.*
import okhttp3.Request

class FirstFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var dataslist: MutableList<DataX>
    private var datalist: Data? = null
    private lateinit var adapter: HomeListAdapter
    private lateinit var recyclershow: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var pages = 0
    private var lastVisibleItem = 0
    private lateinit var layoutManager: LinearLayoutManager


    /**
     * 初始化页面布局
     */
    override fun initView() {
        recyclershow = vieww.findViewById(R.id.homerecyclerview) as RecyclerView
        refreshLayout = vieww.findViewById(R.id.homeswipeRefresh) as SwipeRefreshLayout
        layoutManager = LinearLayoutManager(activity)
        recyclershow.layoutManager = layoutManager
        recyclershow.addItemDecoration(
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

    /**
     * 引入布局
     */
    override fun getMyView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater!!.inflate(R.layout.first_activity, container, false)
    }

    /**
     * 延迟加载的数据
     */
    override fun loadData() {
        homeprogressBar.visibility=View.VISIBLE
        requestNetwork(false)
        recyclershow.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (datalist == null) {
//                        return
//                    }
                    if (adapter.ishasmore() && lastVisibleItem + 1 == adapter.itemCount) {
                        //dataslist
                        Log.e("滑动", "触发再次加载")
                        requestNetwork(false)
                    }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })
    }


    private fun requestNetwork(refresh:Boolean) {
        val observable = Observable.create(ObservableOnSubscribe<JsonData> { emitter ->
            val request = Request.Builder()
                .url("https://www.wanandroid.com/article/list/${pages}/json")
                .header("Cookie",Constant.oldcookie)
                .build()
            val respones = Constant.client.newCall(request).execute()
            val responseData = respones.body?.string()
            if (responseData != null) {
                val gson = Gson()
                val jsonData = gson.fromJson(responseData, JsonData::class.java)
                emitter?.onNext(jsonData)
            }
            emitter?.onComplete()
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
        //观察者
        val observer = object : Observer<JsonData> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(t: JsonData) {
                datalist = t.data
                dataslist = datalist!!.datas
                if (pages == 0) {
                    initrecy()
                } else {
                    updataList()
                }
                pages++
                if (refresh){
                    refreshLayout.isRefreshing = false
                    Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable?) {
                Log.e("onError", "************")
            }
        }
        observable.subscribe(observer)
    }

    private fun initrecy() {
        homeprogressBar.visibility=View.GONE
        adapter = HomeListAdapter(dataslist)
        recyclershow.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(pos: Int, title: String, url: String) {
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra(Constant.CONTENT_URL_KEY, url)
                intent.putExtra(Constant.CONTENT_TITLE_KEY, title)
                startActivity(intent)
            }
            override fun onClick(pos: Int) {

            }
        })

    }

    private fun updataList() {
        adapter.updateList(dataslist, true)
        if (datalist!!.curPage > datalist!!.pageCount) {
            adapter.sethasmore(false)
        }

    }

    //下滑刷新
    override fun onRefresh() {
        refreshLayout.isRefreshing = true
        pages=0
        requestNetwork(true)

    }


}