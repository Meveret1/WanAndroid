package com.linwei.androidclient.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.linwei.androidclient.R
import com.linwei.androidclient.adapter.PageFragmentAdapter
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.fragment.FirstFragment
import com.linwei.androidclient.fragment.FourthFragment
import com.linwei.androidclient.fragment.SecondFragment
import com.linwei.androidclient.fragment.ThirdFragment
import com.yanzhenjie.permission.AndPermission
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_toolbar.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var showhead: CircleImageView
    private lateinit var firstbtn: TextView
    private lateinit var secondbtn: TextView
    private lateinit var thirdbtn: TextView
    private lateinit var fourthbtn: TextView
    private lateinit var signbtn: TextView
    private lateinit var nametext: TextView
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigation: NavigationView
    private lateinit var headview: View
    private lateinit var sp: SharedPreferences
    private lateinit var networkChagerecvicer: NetWorkChangReceiver

    private lateinit var mViewPager: ViewPager
    private val mfragment = ArrayList<Fragment>()
    private lateinit var pageFragmentAdapter: PageFragmentAdapter
    private lateinit var firstFragment: FirstFragment
    private lateinit var secondFragment: SecondFragment
    private lateinit var thirdFragment: ThirdFragment
    private lateinit var fourthFragment: FourthFragment
    private var isRegistered = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constant.getchenjin(this)
        setContentView(R.layout.activity_main)

        initView()
        AndPermission.with(this).permission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
            .onDenied {
                val packageURI = Uri.parse("package:$packageName")
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }.start()
        if (!isNetworkConnected()) {
            Constant.isNetWrokable = false
            AlertDialog.Builder(this).setTitle("提示").setMessage("无网络").setPositiveButton("确认", null)
                .setCancelable(true).create().show()
        } else {

            initClick()
            initViewPager()
            menuItem()
        }
        networkChagerecvicer = NetWorkChangReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChagerecvicer, filter)
        isRegistered = true
    }

    override fun onResume() {
        super.onResume()
        if (Constant.isload) {
            signbtn.text = "注销登录"
            nametext.text = sp.getString("username", "")
        } else {
            nametext.text = ""
            signbtn.text = "点击登录"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegistered) {
            unregisterReceiver(networkChagerecvicer)
        }
    }

    private fun initView() {
        sp = getSharedPreferences("data", Context.MODE_PRIVATE)
        showhead = show_headBar
        firstbtn = first_Bar
        secondbtn = second_Bar
        thirdbtn = third_Bar
        fourthbtn = fourth_Bar
        mDrawerLayout = drawerlayout
        mNavigation = nav_View
        headview = mNavigation.getHeaderView(0)
        signbtn = headview.findViewById(R.id.signText)
        nametext = headview.findViewById(R.id.nameText)
        if (Constant.isload) {
            Log.e("fuck:", "RUN")
            signbtn.text = "注销登录"
            nametext.text = sp.getString("username", "")
        }

    }
    private fun initClick(){
        signbtn.setOnClickListener(this)
        showhead.setOnClickListener(this)
        firstbtn.setOnClickListener(this)
        secondbtn.setOnClickListener(this)
        thirdbtn.setOnClickListener(this)
        fourthbtn.setOnClickListener(this)
    }

    @SuppressLint("InflateParams")
    private fun initViewPager() {
        mViewPager = basePager
        firstFragment = FirstFragment()
        secondFragment = SecondFragment()
        thirdFragment = ThirdFragment()
        fourthFragment = FourthFragment()
        mfragment.add(firstFragment)
        mfragment.add(secondFragment)
        mfragment.add(thirdFragment)
        mfragment.add(fourthFragment)
        pageFragmentAdapter = PageFragmentAdapter(
            supportFragmentManager,
            mfragment,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        mViewPager.adapter = pageFragmentAdapter
        mViewPager.addOnPageChangeListener(changePage())
    }

    private fun menuItem() {
        mNavigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_Favorites -> {
                    startActivity(Intent(this,CollectActivity::class.java))
                }
                R.id.nav_About -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("关于")
                        setMessage("作者：lin")
                        setPositiveButton("确认", null)
                        setCancelable(true)
                        show()
                    }
                }
                R.id.nav_Share -> {
                    Toast.makeText(this, "该功能暂未开放", Toast.LENGTH_LONG).show()
                }
            }
            drawerlayout.closeDrawers()
            true
        }


    }

    private fun changePage(): ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                initblack()
                when (mViewPager.currentItem) {
                    0 -> firstbtn.setTextColor(Color.WHITE)
                    1 -> secondbtn.setTextColor(Color.WHITE)
                    2 -> thirdbtn.setTextColor(Color.WHITE)
                    3 -> fourthbtn.setTextColor(Color.WHITE)
                }
            }

        }

    private fun initblack() {
        firstbtn.setTextColor(Color.BLACK)
        secondbtn.setTextColor(Color.BLACK)
        thirdbtn.setTextColor(Color.BLACK)
        fourthbtn.setTextColor(Color.BLACK)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            firstbtn.id -> {
                mViewPager.currentItem = 0
                firstbtn.setTextColor(Color.WHITE)
            }
            secondbtn.id -> {
                mViewPager.currentItem = 1
                secondbtn.setTextColor(Color.WHITE)
            }
            thirdbtn.id -> {
                mViewPager.currentItem = 2
                thirdbtn.setTextColor(Color.WHITE)
            }
            fourthbtn.id -> {
                mViewPager.currentItem = 3
                fourthbtn.setTextColor(Color.WHITE)
            }
            showhead.id -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
            }
            signbtn.id -> {
                if (Constant.isload) {
                    sp.edit {
                        putString("cookie", "")
                        putString("password", "")
                    }
                    Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show()
                    Constant.isload = false
                    Constant.oldcookie = ""
                    nametext.text = ""
                    signbtn.text = "点击登录"
                } else {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

    private fun isNetworkConnected(): Boolean {
        val mConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.activeNetworkInfo
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable
        }
        return false
    }


    inner class NetWorkChangReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                val info =
                    intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (NetworkInfo.State.CONNECTED == info.state && info.isAvailable) {
                    if (info.type == ConnectivityManager.TYPE_WIFI || info.type == ConnectivityManager.TYPE_MOBILE) {
                        if (!Constant.isNetWrokable)
                            initViewPager()

                    }
                }
            }

        }
    }

}