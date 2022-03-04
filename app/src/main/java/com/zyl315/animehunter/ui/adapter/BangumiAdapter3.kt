package com.zyl315.animehunter.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.ui.activity.PlayActivity
import com.zyl315.animehunter.ui.activity.PlayActivity.Companion.BANGUMI_ID
import com.zyl315.animehunter.ui.adapter.holder.BangumiCover2ViewHolder
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.util.gone

class BangumiAdapter3(
    private val activity: Activity,
    private val dataList: MutableList<BangumiBean> = mutableListOf()
) : BaseRvAdapter(dataList) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BangumiCover2ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bangumi_cover_2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        if (holder is BangumiCover2ViewHolder) {
            holder.apply {
                Glide.with(activity)
                    .load(item.coverUrl)
                    .apply(RequestOptions().transform(RoundedCorners(5.dp)))
                    .into(ivCover)
                tvNewName.text = item.newName
                if (item.newName.isBlank()) tvNewName.gone()
                tvName.text = item.name
            }

            holder.itemView.setOnClickListener {
                startActivity(holder.bindingAdapterPosition)
            }
        }
    }


    fun submitList(newList: List<BangumiBean>) {
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
                putExtra(BANGUMI_ID, dataList[position].bangumiID)
            })
    }
}