package com.zyl315.animehunter.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.view.activity.PlayActivity
import com.zyl315.animehunter.view.adapter.holder.BangumiCover1ViewHolder

class BangumiAdapter(val activity: Activity) :
    ListAdapter<BangumiBean, BangumiCover1ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiCover1ViewHolder {
        return BangumiCover1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bangumi_cover_1, parent, false)
        )

    }

    override fun onBindViewHolder(holderBangumi: BangumiCover1ViewHolder, position: Int) {
        val item = getItem(position)

        holderBangumi.apply {
            Glide.with(activity).load(item.coverUrl).into(ivCover)
            tvNewName.text = item.newName
            if (item.newName.isBlank()) tvNewName.gone()

            tvName.text = item.name
            tvBangumiType.text = item.bangumiType.run { "动画种类：$this" }
            tvPremiereTime.text = item.premiereTime.run { "首播时间：$this" }
            tvPlayStatus.text = item.playStatus.run { "播放状态：$this" }
            tvPlotType.text = item.plotType.run { "剧情类型：$this" }
        }

        holderBangumi.itemView.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    PlayActivity::class.java
                ).apply {
                    putExtra(PlayActivity.BANGUMI_BEAN, item)
                })
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BangumiBean>() {
            override fun areItemsTheSame(oldItem: BangumiBean, newItem: BangumiBean): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BangumiBean, newItem: BangumiBean): Boolean {
                return oldItem == newItem
            }

        }
    }
}