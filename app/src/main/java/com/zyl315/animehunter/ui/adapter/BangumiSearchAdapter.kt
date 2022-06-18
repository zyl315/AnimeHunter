package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.ui.activity.PlayActivity
import com.zyl315.animehunter.ui.adapter.BangumiSearchAdapter.*
import com.zyl315.animehunter.util.dp

class BangumiSearchAdapter(val activity: Activity) :
    ListAdapter<BangumiCoverBean, BangumiCoverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangumiCoverViewHolder {
        return BangumiCoverViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bangumi_cover_3, parent, false)
        )
    }

    override fun onBindViewHolder(holderBangumi: BangumiCoverViewHolder, position: Int) {
        val bangumiCoverBean = getItem(position)

        holderBangumi.apply {
            Glide.with(activity)
                .load(bangumiCoverBean.coverUrl)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                .into(ivCover)
            tvTitle.text = bangumiCoverBean.title
            tvType.text = bangumiCoverBean.type
            tvLastUpdateTime.text = bangumiCoverBean.lastUpdateTime
            tvStatus.text = bangumiCoverBean.status
        }

        holderBangumi.itemView.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    PlayActivity::class.java
                ).apply {
                    putExtra(PlayActivity.BANGUMI_COVER, bangumiCoverBean)
                })
        }
    }

    class BangumiCoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
        val tvLastUpdateTime: TextView = itemView.findViewById(R.id.tv_lastUpdateTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BangumiCoverBean>() {

            override fun areItemsTheSame(
                oldItem: BangumiCoverBean,
                newItem: BangumiCoverBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BangumiCoverBean,
                newItem: BangumiCoverBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}