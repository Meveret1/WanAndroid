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
import com.linwei.androidclient.activity.WebActivity
import com.linwei.androidclient.adapter.DynamicListAdapter
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
import okhttp3.Request

class ThirdFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var dataslist: MutableList<DataX>
    private var datalist: Data? = null
    private lateinit var adapter: DynamicListAdapter
    private lateinit var showlist: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var pages = 0
    private var lastVisibleItem = 0
    private lateinit var layoutManager: LinearLayoutManager

    override fun initView() {
        progressBar = vieww.findViewById(R.id.progressBar3)
        showlist = vieww.findViewById(R.id.ArticleRecycler)
        refreshLayout = vieww.findViewById(R.id.refreshList3)
        layoutManager = LinearLayoutManager(activity)
        showlist.layoutManager = layoutManager
        showlist.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
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
        return inflater!!.inflate(R.layout.third_activity, container, false)
    }

    override fun loadData() {
        requestNetwork()
        showlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (datalist == null) {
                        return
                    }
                    if (adapter.ishasmore() && lastVisibleItem + 1 == adapter.itemCount) {
                        dataslist
                        Log.e("滑动1", "触发再次加载")
                        requestNetwork()
                    }
                    if (!adapter.ishasmore() && lastVisibleItem + 2 == adapter.itemCount) {
                        dataslist.clear()
                        Log.e("滑动2", "触发再次加载")
                        requestNetwork()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })

    }

    private fun requestNetwork() {
        showProgress()
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

        showProgress()

        adapter = DynamicListAdapter(dataslist, true)
        showlist.adapter = adapter
        adapter.setonItemClickListener(object :OnItemClickListener {
            override fun onClick(pos: Int, title: String, url: String) {
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra(Constant.CONTENT_URL_KEY, url)
                intent.putExtra(Constant.CONTENT_TITLE_KEY, title)
                startActivity(intent)
            }
            override fun onClick(pos: Int) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun updataList() {
        adapter.updateList(dataslist, true)
        if (datalist!!.curPage > datalist!!.pageCount) {
            adapter.sethasmore(false)
        }

    }

    private fun showProgress() {
        if (datalist != null) progressBar.visibility = View.GONE
        else progressBar.visibility = View.VISIBLE
    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = true
        pages = 0
        requestNetwork()
        mHandler.postDelayed({
            refreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
        }, 1000)

    }

}