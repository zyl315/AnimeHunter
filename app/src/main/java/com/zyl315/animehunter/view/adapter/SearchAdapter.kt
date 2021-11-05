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
import com.zyl315.animehunter.view.activity.PlayActivity

class SearchAdapter(
    private val activity: Activity,
    private val dataList: List<BangumiBean>
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
                    tvName.text = item.name
                    tvNewName.text = item.newName
                    tvBangumiType.text = item.bangumiType.run { "动画种类：$this" }
                    tvPremiereTime.text = item.premiereTime.run { "首播时间：$this" }
                    tvPlayStatus.text = item.playStatus.run { "播放状态：$this" }
                    tvPlotType.text = item.plotType.run { "剧情类型：$this" }
                }

                holder.itemView.setOnClickListener {
                    activity.startActivity(
                        Intent(
                            activity,
                            PlayActivity::class.java).apply {
                            putExtra("bangumi", item)
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
}