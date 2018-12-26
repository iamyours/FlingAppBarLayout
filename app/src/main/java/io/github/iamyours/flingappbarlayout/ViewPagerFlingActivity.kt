package com.example.yanxx.behaviortest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.activity_viewpager_fling.*

class ViewPagerFlingActivity : AppCompatActivity() {
    val titles = arrayOf(
            "推荐", "热门", "娱乐"
    )
    val fragments = arrayOf(
            RecyclerViewFragment(),
            NestedScrollViewFragment(),
            RecyclerViewFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager_fling)
        viewPager.adapter = MyPagerAdapter()
        tabLayout.setupWithViewPager(viewPager)
    }

    inner class MyPagerAdapter : FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}