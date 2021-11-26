package com.zyl315.animehunter.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zyl315.animehunter.R
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.util.translateTimeUnit
import com.zyl315.animehunter.util.translateWatchPosition
import com.zyl315.animehunter.view.activity.HistoryActivity
import com.zyl315.animehunter.view.activity.PlayActivity
import com.zyl315.animehunter.view.activity.PlayActivity.Companion.BANGUMI_BEAN
import com.zyl315.animehunter.view.adapter.holder.WatchHistoryViewHolder

class WatchHistoryAdapter(
    private val activity: HistoryActivity,
    var dataList: MutableList<WatchHistory>,
) : BaseRvAdapter(dataList) {
    private var selectModel = false
    private var selectSet: MutableSet<WatchHistory>

    init {
        selectModel = activity.viewModel.isSelectModel
        selectSet = activity.viewModel.selectSet
    }

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
                Glide.with(activity)
                    .load(item.bangumiBean.coverUrl)
                    .apply(RequestOptions().transform(RoundedCorners(5.dp)))
                    .into(ivCover)
                tvBangumiTitle.text = item.bangumiBean.name
                tvEpisodeTitle.text = item.episodeName
                tvDuration.text = translateWatchPosition(item.watchedPosition, item.duration)
                tvLastWatchTime.text = translateTimeUnit(item.time)
                rbSelect.apply {
                    if (selectModel) {
                        visibility = View.VISIBLE
                        isChecked = selectSet.contains(item)
                    } else {
                        visibility = View.GONE
                    }
                }
            }
            holder.itemView.setOnClickListener {
                if (selectModel) {
                    if (selectSet.contains(item)) selectSet.remove(item) else selectSet.add(item)
                    activity.viewModel.apply {
                        isSelectedAll.postValue(isSelectAll())
                    }
                    notifyItemChanged(position)
                } else {
                    val intent = Intent(activity, PlayActivity::class.java)
                    intent.putExtra(BANGUMI_BEAN, item.bangumiBean)
                    activity.startActivity(intent)
                }
            }
        }
    }

    fun enterSelectModel() {
        selectModel = true
        notifyDataSetChanged()
    }

    fun exitSelectModel() {
        selectModel = false
        selectSet.clear()
        notifyDataSetChanged()
    }

    fun updateList(watchHistoryList: List<WatchHistory>) {
        val diffResult = DiffUtil.calculateDiff(
            WatchHistoryAdapter.DiffCallBack(
                dataList,
                watchHistoryList
            )
        )
        diffResult.dispatchUpdatesTo(this)
        dataList.apply {
            clear()
            addAll(watchHistoryList)
        }
    }

    class DiffCallBack(
        private var oldDataset: List<WatchHistory>?,
        private val newDataset: List<WatchHistory>?
    ) : DiffUtil.Callback() {


        override fun getOldListSize(): Int {
            return oldDataset?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newDataset?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldItemPosition, newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldWatchHistory = oldDataset?.get(oldItemPosition)
            val newWatchHistory = newDataset?.get(newItemPosition)
            return oldWatchHistory == newWatchHistory
        }
    }
}