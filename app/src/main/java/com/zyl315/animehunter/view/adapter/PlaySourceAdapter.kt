package com.zyl315.animehunter.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.EpisodeBean

class PlaySourceAdapter(
    private var dataList: List<EpisodeBean>,
    var onItemClickListener: onItemClickListener
) : BaseRvAdapter(dataList) {
    private var currentPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaySourceViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_play_url, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        (holder as PlaySourceViewHolder).apply {
            tvEpisode.text = item.title

            if (currentPosition == position) {
                itemView.setBackgroundResource(R.drawable.shape_rectangle_select)
            } else {
                itemView.setBackgroundResource(R.drawable.shape_rectangle_unselect)
            }

            itemView.setOnClickListener {
                currentPosition = position
                onItemClickListener.onItemClick(position)
                notifyDataSetChanged()

            }
        }
    }

    fun updateDataList() {
        currentPosition = 0
        notifyDataSetChanged()
    }
}