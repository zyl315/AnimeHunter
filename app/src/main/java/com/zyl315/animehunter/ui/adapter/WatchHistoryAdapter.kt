package com.zyl315.animehunter.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.ui.activity.HistoryActivity
import com.zyl315.animehunter.ui.activity.PlayActivity
import com.zyl315.animehunter.ui.activity.PlayActivity.Companion.BANGUMI_ID
import com.zyl315.animehunter.ui.adapter.holder.WatchHistoryViewHolder
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.util.translateTimeUnit
import com.zyl315.animehunter.util.translateWatchPosition

class WatchHistoryAdapter(
    private val activity: HistoryActivity,
) : ListAdapter<WatchHistory, WatchHistoryViewHolder>(DIFF_CALLBACK) {
    private var isSelectModel = false
    private var selectSet: MutableSet<WatchHistory>

    init {
        isSelectModel = activity.viewModel.isSelectModel
        selectSet = activity.viewModel.selectSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchHistoryViewHolder {
        return WatchHistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_play_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WatchHistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            Glide.with(activity)
                .load(item.bangumiBean.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(5.dp)))
                .into(ivCover)
            tvBangumiTitle.text = item.bangumiBean.name
            tvEpisodeTitle.text = item.episodeName
            tvDuration.text = translateWatchPosition(item.watchedPosition, item.duration)
            tvLastWatchTime.text = translateTimeUnit(item.time)
            rbSelect.apply {
                if (isSelectModel) {
                    visibility = View.VISIBLE
                    isChecked = selectSet.contains(item)
                } else {
                    visibility = View.GONE
                }
            }
        }
        holder.itemView.setOnClickListener {
            if (isSelectModel) {
                if (selectSet.contains(item)) selectSet.remove(item) else selectSet.add(item)
                activity.viewModel.apply {
                    isSelectedAll.postValue(isSelectAll())
                }
                notifyItemChanged(holder.layoutPosition)
            } else {
                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra(BANGUMI_ID, item.bangumiBean.bangumiID)
                activity.startActivity(intent)
            }
        }

        holder.itemView.setOnLongClickListener {
            true
        }

    }

    fun enterSelectModel() {
        isSelectModel = true
        notifyDataSetChanged()
    }

    fun exitSelectModel() {
        isSelectModel = false
        selectSet.clear()
        notifyDataSetChanged()
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WatchHistory>() {
            override fun areItemsTheSame(oldItem: WatchHistory, newItem: WatchHistory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: WatchHistory, newItem: WatchHistory): Boolean {
                return oldItem == newItem
            }

        }
    }
}