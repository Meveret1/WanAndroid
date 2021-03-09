package com.linwei.androidclient.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.R
import com.linwei.androidclient.adapter.CollectListAdapter
import com.linwei.androidclient.adapter.OnItemClickListener
import com.linwei.androidclient.bean.*
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.model.CollectModel
import kotlinx.android.synthetic.main.collect_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var adapter: CollectListAdapter
    private lateinit var dataslist: MutableList<CollectData2>
    private var datalist: CollectData1? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var collectModel: CollectModel
    private var pages = 0
    private var refresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collect_activity)
        initview()
    }

    override fun onResume() {
        super.onResume()
        collectModel.getcollectList(pages)

    }

    private fun initview() {
        layoutManager = LinearLayoutManager(this)
        collectRecycler.layoutManager = layoutManager
        collectRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        collectModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(CollectModel::class.java)
        collectModel.collectData.observe(this, liveobserver())

        initRefresh()

    }

    private fun initRefresh() {
        collectSwipRefresh.setColorSchemeResources(
            android.R.color.holo_blue_light, android.R.color.holo_red_light,
            android.R.color.holo_orange_light, android.R.color.holo_green_light
        )
        collectSwipRefresh.setOnRefreshListener(this)
    }

    private fun liveobserver() = Observer<CollectData> {
        datalist = it.data
        dataslist = datalist!!.datas

        if (pages == 0) {
            initrecy()
        } else {
            updataList()
        }
        if (refresh) {
            collectSwipRefresh.isRefreshing = false
            Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show()
            refresh = false
        }
        pages++
    }

    private fun initrecy() {

        adapter = CollectListAdapter(dataslist, true)
        collectRecycler.adapter = adapter
        adapter.setonItemClickListener(object : OnItemClickListener {
            override fun onClick(pos: Int, title: String, url: String) {
                val intent = Intent(this@CollectActivity, WebActivity::class.java)
                intent.putExtra(Constant.CONTENT_URL_KEY, url)
                intent.putExtra(Constant.CONTENT_TITLE_KEY, title)
                startActivity(intent)
            }

            override fun onClickLike(pos: Int) {
                Constant.run {
                    retrofithelper.removeCollect(oldcookie, dataslist[pos].originId)
                        .enqueue(object : Callback<JsonData> {
                            override fun onFailure(call: Call<JsonData>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<JsonData>,
                                response: Response<JsonData>
                            ) {
                                val json = response.body()
                                if (json!!.errorCode == 0) {
                                    Toast.makeText(MyApplication.context, "取消收藏", Toast.LENGTH_SHORT).show()
                                    runOnUiThread {
                                        collectSwipRefresh.isRefreshing = true
                                        pages=0
                                        collectModel.getcollectList(pages)
                                        refresh = true
                                    }


                                }

                            }
                        })
                }

            }
        })
    }

    private fun updataList() {
        adapter.updateList(dataslist, true)
        if (datalist!!.curPage > datalist!!.pageCount) {
            adapter.sethasmore(false)
        }


    }

    override fun onRefresh() {
        collectSwipRefresh.isRefreshing = true
        refresh = true
        pages = 0
        collectModel.getcollectList(pages)
    }
}