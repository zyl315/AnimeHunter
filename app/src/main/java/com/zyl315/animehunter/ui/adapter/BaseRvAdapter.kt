package com.zyl315.animehunter.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.bean.BaseBean

abstract class BaseRvAdapter(private var dataList: List<BaseBean>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }
}