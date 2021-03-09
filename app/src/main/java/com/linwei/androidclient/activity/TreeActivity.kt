package com.linwei.androidclient.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.linwei.androidclient.R
import com.linwei.androidclient.bean.Data1
import com.linwei.androidclient.bean.TreeData
import com.linwei.androidclient.constant.Constant
import com.linwei.androidclient.fragment.TreeFragment
import kotlinx.android.synthetic.main.aritcle_activity.*
import java.util.ArrayList

class TreeActivity:AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var bodystr:String
    private var id:Int=0
    private lateinit var firstLevel: List<Data1>
    private val tabFragment=ArrayList<Fragment>()
    private val tabs=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aritcle_activity)
        initView()



    }

    private fun initView(){
        tabLayout=tab_Layout
        viewPager=article_ViewPaper

        bodystr=intent.getStringExtra(Constant.CONTENT_BODY_KEY)!!
        id=intent.getIntExtra(Constant.CONTENT_ID_KEY,0)
        val gson = Gson()
        val treedata = gson.fromJson(bodystr, TreeData::class.java)
        firstLevel=treedata.data
        firstLevel[id].children.forEach {chiled->
            tabs.add(chiled.name)
            tabFragment.add(TreeFragment(chiled.id))
        }
        tabs.forEach {str->
            tabLayout.addTab(tabLayout.newTab().setText(str))
        }
        viewPager.adapter=object :FragmentStatePagerAdapter(supportFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
            override fun getItem(position: Int): Fragment {
                return tabFragment[position]
            }

            override fun getCount(): Int {
                return tabFragment.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabs[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            }
        }
        tabLayout.setupWithViewPager(viewPager,false)
    }

}