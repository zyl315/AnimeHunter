package com.zyl315.animehunter.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zyl315.animehunter.R
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.view.adapter.holder.WatchHistoryViewHolder
import java.util.*

class WatchHistoryAdapter(
    private val activity: Activity,
    private val dataList: List<WatchHistory>
) : BaseRvAdapter(dataList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WatchHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_play_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        if (holder is WatchHistoryViewHolder) {
            holder.apply {
                Glide.with(activity).load(item.coverUrl).into(ivCover)
                tvBangumiTitle.text = item.bangumiTitle
                tvEpisodeTitle.text = item.episodeName
                tvLastWatchTime.text = Date(item.watchTime).toString()
            }
        }
    }
}