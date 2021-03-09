package com.linwei.androidclient.adapter

interface OnItemClickListener {
    fun onClick(pos:Int,title:String,url:String){}
    fun onClick(pos: Int){}
    fun onClickLike(pos: Int){}
}