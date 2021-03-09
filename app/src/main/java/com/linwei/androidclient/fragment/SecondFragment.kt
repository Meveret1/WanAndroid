package com.linwei.androidclient.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.linwei.androidclient.R
import com.linwei.androidclient.activity.WebActivity
import com.linwei.androidclient.adapter.OnItemClickListener
import com.linwei.androidclient.adapter.ProjectListAdapter
import com.linwei.androidclient.base.BaseFragment
import com.linwei.androidclient.bean.Data
import com.linwei.androidclient.bean.DataX
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.model.ProjectModel


class SecondFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var dataslist: MutableList<DataX>
    private var datalist: Data? = null

    private lateinit var showlist: RecyclerView
    private lateinit var adapter: ProjectListAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private var pages = 0
    private var lastVisibleItem = 0
    private var refresh=false
    private lateinit var projectModel: ProjectModel


    override fun initView() {
        showlist = vieww.findViewById(R.id.ObjectRecycler)
        progressBar = vieww.findViewById(R.id.progressBar2)
        refreshLayout = vieww.findViewById(R.id.refreshList2)
        projectModel = ViewModelProvider.AndroidViewModelFactory(activity!!.application)
            .create(ProjectModel::class.java)
        projectModel.jsonData.observe(activity!!, liveobserver())
        initRefreshLayout()
        initRecycler()
    }

    private fun initRefreshLayout() {
        refreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light, android.R.color.holo_red_light,
            android.R.color.holo_orange_light, android.R.color.holo_green_light
        )
        refreshLayout.setOnRefreshListener(this)

    }

    private fun initRecycler() {
        layoutManager = LinearLayoutManager(activity)
        showlist.layoutManager = layoutManager
        showlist.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    override fun getMyView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater!!.inflate(R.layout.second_activity, container, false)
    }

    /**
     * 懒加载
     */
    override fun loadData() {
        projectModel.getProjectList(pages)
        showlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (datalist == null) {
                        return
                    }
                    if (adapter.ishasmore() && lastVisibleItem + 1 == adapter.itemCount) {
                        Log.e("滑动1", "触发再次加载")
                        projectModel.getProjectList(pages)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })
    }

    private fun liveobserver() = Observer<JsonData> {
        datalist = it.data
        dataslist = datalist!!.datas
        if (pages == 0) {
            initrecy()
        } else {
            updataList()
        }
        if (refresh){
            refreshLayout.isRefreshing = false
            Toast.makeText(activity, "刷新成功", Toast.LENGTH_SHORT).show()
            refresh=false
        }
        pages++
    }

    private fun initrecy() {
        showProgress()
        adapter = ProjectListAdapter(dataslist, true)
        showlist.adapter = adapter
        adapter.setonItemClickListener(object : OnItemClickListener {
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
        Log.e("page", datalist?.curPage.toString() + "  " + datalist!!.pageCount.toString())
        adapter.updateList(dataslist, true,refresh)
        if (datalist!!.curPage > datalist!!.pageCount) {
            adapter.sethasmore(false)
        }
    }

    private fun showProgress() {
        if (datalist != null) progressBar.visibility = View.GONE
        else progressBar.visibility = View.VISIBLE
    }

    /**
     * 下滑刷新
     */
    override fun onRefresh() {
        refreshLayout.isRefreshing = true
        refresh=true
        pages = 0
        projectModel.getProjectList(pages)
    }
}