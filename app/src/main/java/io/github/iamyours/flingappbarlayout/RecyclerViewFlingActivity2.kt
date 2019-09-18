package io.github.iamyours.flingappbarlayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import com.example.yanxx.behaviortest.TestAdapter
import kotlinx.android.synthetic.main.activity_recyclerview_fling.*

class RecyclerViewFlingActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_fling)
        recyclerView.adapter = TestAdapter()
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

}