package com.example.yanxx.behaviortest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.fragment_nestedscrollview.*

class NestedScrollViewFragment : Fragment() {
    private var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_nestedscrollview, null)
        }
        val parent = rootView!!.parent as? ViewGroup
        parent?.removeView(rootView)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(img2).load(R.drawable.img2).into(img2)
        Glide.with(img3).load(R.drawable.img3).into(img3)
    }
}