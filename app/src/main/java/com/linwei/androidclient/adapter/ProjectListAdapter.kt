package com.linwei.androidclient.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linwei.androidclient.MyApplication
import com.linwei.androidclient.R
import com.linwei.androidclient.bean.DataX
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectListAdapter(private val list: MutableList<DataX>, hasMore: Boolean) :
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
        newDatas: List<DataX>?,
        hasMore: Boolean,
        refresh:Boolean
    ) {
        if (refresh){
            list.clear()
        }
        if (newDatas != null) {
            list += newDatas
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

        val texttitle = v.findViewById(R.id.textTitle) as TextView
        val textdescription = v.findViewById(R.id.textDescription) as TextView
        val textauther = v.findViewById(R.id.textAuther) as TextView
        val texttime = v.findViewById(R.id.textTime) as TextView
        val imageshow = v.findViewById(R.id.imageShow) as ImageView
        val collected = v.findViewById(R.id.collected) as ImageView
        val imageprogressbar = v.findViewById(R.id.imageProgressBar) as ProgressBar

    }

    class TipsHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tipstext = v.findViewById(R.id.tips) as TextView
        val progressbar2 = v.findViewById<ProgressBar>(R.id.hasmoreProgress)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == roothodler) RootHolder(
            LayoutInflater.from(context).inflate(R.layout.my_object_list, parent, false)
        )
        else TipsHolder(
            LayoutInflater.from(context).inflate(R.layout.hasmore_data_item, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int =
        if (position == itemCount - 1) tipsHolder else roothodler

    override fun getItemCount(): Int {
        return list.size + 1

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RootHolder) {
            val jsonstr = list[position]
            holder.texttitle.text = jsonstr.title
            holder.textdescription.text = jsonstr.desc
            holder.textauther.text = jsonstr.author
            holder.texttime.text = jsonstr.niceDate
            if (jsonstr.collect) {
                holder.collected.setImageResource(R.mipmap.collected)
            } else {
                holder.collected.setImageResource(R.mipmap.not_collected)
            }
            holder.collected.setOnClickListener {
                if (jsonstr.collect) {
                    Constant.retrofithelper.removeCollect(Constant.oldcookie,jsonstr.id).enqueue(object :
                        Callback<JsonData> {
                        override fun onResponse(
                            call: Call<JsonData>,
                            response: Response<JsonData>
                        ) {
                            Toast.makeText(MyApplication.context,"取消收藏", Toast.LENGTH_SHORT).show()
                            holder.collected.setImageResource(R.mipmap.not_collected)
                            jsonstr.collect = false
                        }

                        override fun onFailure(call: Call<JsonData>, t: Throwable) {
                            Toast.makeText(MyApplication.context,"取消收藏Failed", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Constant.run {
                        retrofithelper.addCollect(oldcookie,jsonstr.id).enqueue(object :Callback<JsonData>{
                            override fun onResponse(
                                call: Call<JsonData>,
                                response: Response<JsonData>
                            ) {
                                holder.collected.setImageResource(R.mipmap.collected)
                                jsonstr.collect = true
                                Toast.makeText(MyApplication.context,"已收藏",Toast.LENGTH_SHORT).show()
                            }

                            override fun onFailure(call: Call<JsonData>, t: Throwable) {
                                Toast.makeText(MyApplication.context,"收藏失败",Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
            Glide.with(context)
                .load(jsonstr.envelopePic)
                .dontAnimate()
                .into(holder.imageshow)
            if (holder.imageshow.drawable == null)
                holder.imageprogressbar.visibility = View.VISIBLE
            else holder.imageprogressbar.visibility = View.GONE
            holder.itemView.setOnClickListener {
                onItemClickListener.onClick(position, jsonstr.title, jsonstr.link)
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