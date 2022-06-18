package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.HomeContentBean
import com.zyl315.animehunter.ui.adapter.holder.HomeContentViewHolder

class HomeContentAdapter(
    val activity: Activity,
    private val dataList: MutableList<HomeContentBean> = mutableListOf()
) : BaseRvAdapter(dataList) {
    private var spanCount = 3
    private lateinit var bangumiAdapter: BangumiAdapter3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = HomeContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_content_recycle_view, parent, false)
        )

        holder.apply {
            rvBangumi.layoutManager = GridLayoutManager(activity, spanCount)
            rvBangumi.setHasFixedSize(true)
            bangumiAdapter = BangumiAdapter3(activity)
            rvBangumi.adapter = bangumiAdapter
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeContentBean = dataList[position]
        if (holder is HomeContentViewHolder) {
            holder.apply {
                tvTitle.text = homeContentBean.name
                bangumiAdapter.submitList(subList(homeContentBean.bangumiCoverBeanList))
            }
        }
    }

    private fun subList(list: MutableList<BangumiCoverBean>): MutableList<BangumiCoverBean> {
        return if (list.size < spanCount) {
            list
        } else {
            list.subList(0, list.size - list.size % spanCount)
        }
    }

    fun submitList(newList: List<HomeContentBean>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}