package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.BangumiCoverBean
import com.zyl315.animehunter.bean.age.BangumiDetailBean
import com.zyl315.animehunter.ui.activity.PlayActivity
import com.zyl315.animehunter.ui.activity.PlayActivity.Companion.BANGUMI_COVER
import com.zyl315.animehunter.ui.adapter.holder.BangumiCover1ViewHolder
import com.zyl315.animehunter.ui.adapter.holder.BangumiCover2ViewHolder
import com.zyl315.animehunter.ui.adapter.holder.EmptyViewHolder
import com.zyl315.animehunter.ui.adapter.holder.ViewHolderType
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.util.gone

class BangumiAdapter2(
    private val activity: Activity,
    private val coverType: Int,
    private val dataList: MutableList<BangumiCoverBean> = mutableListOf()
) : BaseRvAdapter(dataList) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewHolderType.BANGUMI_COVER_1 ->
                BangumiCover1ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bangumi_cover_3, parent, false)
                )

            ViewHolderType.BANGUMI_COVER_2 -> {
                BangumiCover2ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bangumi_cover_2, parent, false)
                )
            }
            else ->
                EmptyViewHolder(View(parent.context))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bangumiCoverBean = dataList[position]
        when (holder) {
            is BangumiCover1ViewHolder -> {
                holder.apply {
                    Glide.with(activity)
                        .load(bangumiCoverBean.coverUrl)
                        .apply(RequestOptions().transform(RoundedCorners(5.dp)))
                        .into(ivCover)
                    tvStatus.text = bangumiCoverBean.status
                    tvLastUpdateTime.text = bangumiCoverBean.lastUpdateTime
                    tvTitle.text = bangumiCoverBean.title
                    tvType.text = bangumiCoverBean.type
                    tvStatus.text = bangumiCoverBean.status
                }

                holder.itemView.setOnClickListener {
                    startActivity(holder.bindingAdapterPosition)
                }
            }

            is BangumiCover2ViewHolder -> {
                holder.apply {
                    Glide.with(activity)
                        .load(bangumiCoverBean.coverUrl)
                        .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
                        .into(ivCover)
                    tvStatus.text = bangumiCoverBean.status
                    tvTitle.text = bangumiCoverBean.title
                }

                holder.itemView.setOnClickListener {
                    startActivity(holder.bindingAdapterPosition)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return coverType
    }

    fun submitList(newList: List<BangumiCoverBean>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun startActivity(position: Int) {
        activity.startActivity(
            Intent(
                activity,
                PlayActivity::class.java
            ).apply {
                putExtra(BANGUMI_COVER, dataList[position])
            })
    }
}