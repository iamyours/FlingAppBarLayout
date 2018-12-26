package io.github.iamyours.flingappbarlayout

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.yanxx.behaviortest.NestedScrollFlingActivity
import com.example.yanxx.behaviortest.RecyclerViewFlingActivity
import com.example.yanxx.behaviortest.ViewPagerFlingActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
    }

    private fun initListeners() {
        nestedScrollViewBtn.setOnClickListener {
            toActivity(NestedScrollFlingActivity::class.java)
        }
        recyclerViewBtn.setOnClickListener {
            toActivity(RecyclerViewFlingActivity::class.java)
        }
        viewPagerBtn.setOnClickListener {
            toActivity(ViewPagerFlingActivity::class.java)
        }
    }

    private fun toActivity(clazz: Class<out Activity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}
