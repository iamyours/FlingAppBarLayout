package com.example.yanxx.behaviortest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.activity_smart_refresh_layout_fling.*

class SmartRefreshLayoutFlingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_refresh_layout_fling)
        Glide.with(img1).load(R.drawable.img1).into(img1)
        Glide.with(img2).load(R.drawable.img2).into(img2)
        Glide.with(img3).load(R.drawable.img3).into(img3)
        Glide.with(img_tab).load(R.drawable.tab).into(img_tab)
    }

}