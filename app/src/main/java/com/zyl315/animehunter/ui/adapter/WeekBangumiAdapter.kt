package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.BangumiWeekListBean
import com.zyl315.animehunter.ui.activity.PlayActivity

class WeekBangumiAdapter(
    private val activity: Activity,
    private val dataList: MutableList<BangumiWeekListBean> = mutableListOf()
) :
    BaseRvAdapter(dataList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeekBangumiViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_week_bangumi, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bangumi = dataList[position]
        if (holder is WeekBangumiViewHolder) {
            holder.tvName.text = bangumi.name
            holder.itemView.setOnClickListener {
                activity.startActivity(
                    Intent(
                        activity,
                        PlayActivity::class.java
                    ).apply {
                        putExtra(PlayActivity.BANGUMI_ID, dataList[position].bangumiID)
                    })
            }
        }
    }

    fun submitList(newList: MutableList<BangumiWeekListBean>?) {
        if (newList != null) {
            dataList.clear()
            dataList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    inner class WeekBangumiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}