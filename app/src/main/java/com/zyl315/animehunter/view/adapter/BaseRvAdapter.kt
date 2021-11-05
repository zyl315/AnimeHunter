package com.zyl315.animehunter.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.bean.BaseBean

abstract class BaseRvAdapter(private val dataList: List<BaseBean>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }
}