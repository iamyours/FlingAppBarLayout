package com.example.yanxx.behaviortest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iamyours.flingappbarlayout.R
import kotlinx.android.synthetic.main.item.view.*


class TestAdapter : RecyclerView.Adapter<TestAdapter.Holder>() {
    val data = arrayOf("Java", "C", "C++", "Python", "C#", "VB.Net", "PHP",
            "JavaScript", "Ruby", "SQL", "Pert", "Swift", "Delphi", "Objective-C", "VB", "Go", "MATLAB", "PL/SQL", "R",
            "SAS", "Dart", "COBOL", "D", "Lua", "ABAP", "Fortran", "Transact-SQL", "Scratch", "Scala", "Apex", "Prolog", "Ada", "Lisp",
            "F#", "LabVIEW", "Julia", "Kotlin", "Logo", "Ladder Logic", "Haskell", "Alice", "Bash", "ActionScript", "Ring", "Erlan",
            "Clojure", "RPG(OS/400)", "OpenEdge ABL", "PL/I"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return Holder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.tv_name.text = data[position]
    }

    inner class Holder(v: View) : RecyclerView.ViewHolder(v) {

    }
}