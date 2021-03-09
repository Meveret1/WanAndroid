package com.linwei.androidclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.linwei.androidclient.R
import com.linwei.androidclient.bean.CollectData2

class CollectListAdapter(private val list: MutableList<CollectData2>, hasMore: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var onItemClickListener: OnItemClickListener


    private var roothodler = 0
    private val tipsHolder = 1
    private var hasMore = true//还有更多

    init {
        this.hasMore = hasMore
    }

    fun setonItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun updateList(
        newDatas: List<CollectData2>?,
        hasMore: Boolean
    ) {
        if (newDatas != null) {
            list.addAll(newDatas)
        }
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    fun ishasmore(): Boolean {
        return hasMore
    }

    fun sethasmore(hasMore: Boolean) {
        this.hasMore = hasMore
    }

    class RootHolder(v: View) : RecyclerView.ViewHolder(v) {

        val texttitle = v.findViewById(R.id.aritcle_Title) as TextView
        val texttime = v.findViewById(R.id.article_Time) as TextView
        val cricleimage = v.findViewById(R.id.textchapterName) as TextView
        val collected = v.findViewById<ImageView>(R.id.collected)!!

    }

    class TipsHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tipstext = v.findViewById(R.id.tips) as TextView
        val progressbar2 = v.findViewById<ProgressBar>(R.id.hasmoreProgress)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == roothodler) RootHolder(
            LayoutInflater.from(context).inflate(R.layout.my_aritcle_list, parent, false)
        )
        else TipsHolder(
            LayoutInflater.from(context).inflate(R.layout.hasmore_data_item, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.size > 14 && position == itemCount - 1) tipsHolder
        else roothodler
    }


    override fun getItemCount(): Int {
        return if (list.size > 14) {
            list.size + 1
        } else {
            list.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RootHolder) {
            if (position < list.size) {
                val jsonstr = list[position]
                holder.texttitle.text = jsonstr.title
                holder.texttime.text = jsonstr.niceDate
                holder.cricleimage.text = jsonstr.chapterName
                holder.itemView.setOnClickListener {
                    onItemClickListener.onClick(position, jsonstr.title, jsonstr.link)
                }
                    holder.collected.setImageResource(R.mipmap.collected)

                holder.collected.setOnClickListener {
                    onItemClickListener.onClickLike(position)
                }
            }
        } else if (holder is TipsHolder) {
            if (hasMore) {
                if (list.size > 0) holder.tipstext.text = "加载中..."
            } else {
                holder.tipstext.text = "没有更多数据了"
                holder.progressbar2.visibility = View.GONE
            }
        }
    }


}