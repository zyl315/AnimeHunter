package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.BangumiDetailBean
import com.zyl315.animehunter.ui.activity.PlayActivity
import com.zyl315.animehunter.ui.adapter.holder.BangumiCover1ViewHolder
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.util.gone

class BangumiAdapter(val activity: Activity) :
    ListAdapter<BangumiDetailBean, BangumiCover1ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiCover1ViewHolder {
        return BangumiCover1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bangumi_cover_1, parent, false)
        )

    }

    override fun onBindViewHolder(holderBangumi: BangumiCover1ViewHolder, position: Int) {
        val item = getItem(position)

        holderBangumi.apply {
            Glide.with(activity)
                .load(item.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(5.dp)))
                .into(ivCover)
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
                    putExtra(PlayActivity.BANGUMI_ID, item.bangumiID)
                })
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BangumiDetailBean>() {
            override fun areItemsTheSame(
                oldItem: BangumiDetailBean,
                newItem: BangumiDetailBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BangumiDetailBean,
                newItem: BangumiDetailBean
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}