package com.example.yanxx.behaviortest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class RecyclerViewFragment : Fragment() {
    private var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_recyclerview, null)
        }
        val parent = rootView!!.parent as? ViewGroup
        parent?.removeView(rootView)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = TestAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }
}