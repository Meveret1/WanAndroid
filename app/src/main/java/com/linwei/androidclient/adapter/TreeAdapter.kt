package com.linwei.androidclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linwei.androidclient.R
import com.linwei.androidclient.bean.Data1

class TreeAdapter(val list: List<Data1>) : RecyclerView.Adapter<TreeAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val onetitle = v.findViewById<TextView>(R.id.oneTitle)!!
        val twotitle = v.findViewById<TextView>(R.id.twoTitle)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.my_tree_list, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        var twostr = ""
        holder.onetitle.text = data.name
        data.children.forEach {
            twostr = twostr + it.name + "  "
        }
        holder.twotitle.text = twostr
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(position)
        }
    }


}