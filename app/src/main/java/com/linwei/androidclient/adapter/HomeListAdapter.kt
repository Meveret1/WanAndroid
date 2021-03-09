package com.linwei.androidclient.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.linwei.androidclient.activity.WebActivity
import com.linwei.androidclient.bean.BannerData
import com.linwei.androidclient.bean.Bannerlist
import com.linwei.androidclient.bean.DataX
import com.linwei.androidclient.bean.JsonData
import com.linwei.androidclient.constant.Constant
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeListAdapter(private val list: MutableList<DataX>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val headtype = 1
    private val roottype = 2
    private val tipstape = 3
    private var hasMore = true
    private var firstload = true
    private lateinit var listbanner: List<Bannerlist>

    private lateinit var context: Context
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    class HeadHodler(v: View) : RecyclerView.ViewHolder(v) {
        val banner1 = v.findViewById<Banner>(R.id.banner1)!!
    }

    class RootHodler(v: View) : RecyclerView.ViewHolder(v) {
        val texttitle = v.findViewById(R.id.aritcle_Title) as TextView
        val texttime = v.findViewById(R.id.article_Time) as TextView
        val cricleimage = v.findViewById(R.id.textchapterName) as TextView
        val collected = v.findViewById<ImageView>(R.id.collected)!!
    }

    class TipsHodler(v: View) : RecyclerView.ViewHolder(v) {
        val tipstext = v.findViewById(R.id.tips) as TextView
        val progressbar2 = v.findViewById<ProgressBar>(R.id.hasmoreProgress)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            headtype -> HeadHodler(
                LayoutInflater.from(context).inflate(R.layout.banner_view, parent, false)
            )
            tipstape -> TipsHodler(
                LayoutInflater.from(context).inflate(R.layout.hasmore_data_item, parent, false)
            )
            else -> RootHodler(
                LayoutInflater.from(context).inflate(R.layout.my_aritcle_list, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = list.size + 2
    override fun getItemViewType(position: Int): Int {

        return when (position) {
            0 -> headtype
            itemCount - 1 -> tipstape
            else -> roottype
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeadHodler) {
            if (firstload) {
                requestNetwork(holder.banner1)
                firstload = false
            }
        } else if (holder is RootHodler) {
            if (position < list.size + 2) {
                val jsonstr = list[position - 1]
                holder.texttitle.text = jsonstr.title
                holder.texttime.text = jsonstr.niceDate
                holder.cricleimage.text = jsonstr.chapterName
                holder.itemView.setOnClickListener {
                    onItemClickListener.onClick(position, jsonstr.title, jsonstr.link)
                }
                if (jsonstr.collect) {
                    holder.collected.setImageResource(R.mipmap.collected)
                } else {
                    holder.collected.setImageResource(R.mipmap.not_collected)
                }
                holder.collected.setOnClickListener {
                    if (jsonstr.collect) {
                        Constant.retrofithelper.removeCollect(Constant.oldcookie, jsonstr.id)
                            .enqueue(object : Callback<JsonData> {
                                override fun onResponse(
                                    call: Call<JsonData>,
                                    response: Response<JsonData>
                                ) {
                                    if (response.body()!!.errorCode == 0) {
                                        Toast.makeText(
                                            MyApplication.context,
                                            "取消收藏",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else Toast.makeText(
                                        MyApplication.context,
                                        "你怕是没有登录哦！！",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                }

                                override fun onFailure(call: Call<JsonData>, t: Throwable) {
                                    Toast.makeText(
                                        MyApplication.context,
                                        "请求失败",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                        holder.collected.setImageResource(R.mipmap.not_collected)
                        jsonstr.collect = false
                    } else {
                        holder.collected.setImageResource(R.mipmap.collected)
                        Constant.retrofithelper.addCollect(Constant.oldcookie, jsonstr.id)
                            .enqueue(object : Callback<JsonData> {
                                override fun onResponse(
                                    call: Call<JsonData>,
                                    response: Response<JsonData>
                                ) {
                                    if (response.body()!!.errorCode == 0) {
                                        Toast.makeText(
                                            MyApplication.context,
                                            "收藏成功",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else Toast.makeText(
                                        MyApplication.context,
                                        "你怕是没有登录哦！！",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFailure(call: Call<JsonData>, t: Throwable) {
                                    Toast.makeText(
                                        MyApplication.context,
                                        "请求失败",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        jsonstr.collect = true
                    }
                }
            }
        } else if (holder is TipsHodler) {
            if (hasMore) {
                if (list.size > 0) holder.tipstext.text = "加载中..."
            } else {
                holder.tipstext.text = "没有更多数据了"
                holder.progressbar2.visibility = View.GONE
            }
        }
    }

    fun updateList(
        newDatas: List<DataX>?,
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

    private fun requestNetwork(banner1: Banner) {
        Constant.retrofithelper.getBanner().enqueue(object : Callback<BannerData> {
            override fun onResponse(call: Call<BannerData>, response: Response<BannerData>) {
                listbanner = response.body()!!.data
                initBanner(banner1)
            }

            override fun onFailure(call: Call<BannerData>, t: Throwable) {
                Toast.makeText(MyApplication.context, "获取Banner失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initBanner(banner1: Banner) {
        val titlelist = ArrayList<String>()
        val imagelist = ArrayList<String>()
        listbanner.forEach {
            titlelist.add(it.title)
            imagelist.add(it.imagePath)
        }
        banner1.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
        banner1.setBannerTitles(titlelist)
        banner1.setImages(imagelist)
        banner1.setImageLoader(MyLoader())
        banner1.setBannerAnimation(Transformer.Default)
        banner1.setOnBannerListener {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(Constant.CONTENT_URL_KEY, listbanner[it].url)
            intent.putExtra(Constant.CONTENT_TITLE_KEY, listbanner[it].title)
            context.startActivity(intent)
        }
        banner1.setDelayTime(5000)
        banner1.isAutoPlay(true)
        banner1.setIndicatorGravity(BannerConfig.CENTER)
        banner1.start()
    }

    /**
     * 加载图片
     */
    class MyLoader : ImageLoader() {
        override fun displayImage(context: Context, path: Any?, imageView: ImageView) {
            Glide.with(context).load(path).into(imageView)
        }
    }


}