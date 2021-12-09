package com.zyl315.animehunter.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.view.activity.PlayActivity
import com.zyl315.animehunter.view.activity.PlayActivity.Companion.BANGUMI_BEAN
import com.zyl315.animehunter.view.adapter.holder.CoverViewHolder
import com.zyl315.animehunter.view.adapter.holder.EmptyViewHolder

class SearchAdapter(
    private val activity: Activity,
    private var dataList: MutableList<BangumiBean> = mutableListOf()
) : BaseRvAdapter(dataList) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Const.ViewHolderType.BangumiCover ->
                CoverViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bangumi_cover, parent, false)
                )
            else ->
                EmptyViewHolder(View(parent.context))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is CoverViewHolder -> {
                holder.apply {
                    Glide.with(activity).load(item.coverUrl).into(ivCover)
                    tvNewName.text = item.newName
                    if (item.newName.isBlank()) tvNewName.gone()

                    tvName.text = item.name
                    tvBangumiType.text = item.bangumiType.run { "动画种类：$this" }
                    tvPremiereTime.text = item.premiereTime.run { "首播时间：$this" }
                    tvPlayStatus.text = item.playStatus.run { "播放状态：$this" }
                    tvPlotType.text = item.plotType.run { "剧情类型：$this" }
                }

                holder.itemView.setOnClickListener {
                    activity.startActivity(
                        Intent(
                            activity,
                            PlayActivity::class.java
                        ).apply {
                            putExtra(BANGUMI_BEAN, item)
                        })
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) {
            Const.ViewHolderType.BangumiCover
        } else {
            Const.ViewHolderType.UNKNOWN
        }
    }

    fun insertList(newList: List<BangumiBean>) {
        val oldListSize = dataList.size
        dataList.clear()
        dataList.addAll(newList)
        notifyItemRangeInserted(oldListSize, newList.size - oldListSize)
    }

    fun submitList(newList: List<BangumiBean>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}