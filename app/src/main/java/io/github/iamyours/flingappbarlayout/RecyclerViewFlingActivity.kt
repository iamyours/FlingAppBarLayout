package com.example.yanxx.behaviortest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.activity_recyclerview_fling.*

class RecyclerViewFlingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_fling)
        recyclerView.adapter = TestAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

}