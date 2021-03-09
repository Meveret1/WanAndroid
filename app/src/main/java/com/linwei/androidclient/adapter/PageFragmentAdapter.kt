package com.linwei.androidclient.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageFragmentAdapter(
    fm: FragmentManager,
    private val fragments: List<Fragment>,
    behavior: Int
) : FragmentPagerAdapter(fm, behavior) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }
}