package com.zyl315.animehunter.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.view.adapter.holder.PlaySourceViewHolder

class PlaySourceAdapter(
    private var dataList: List<EpisodeBean>,
    var orientation: Int,
    var currentPosition: Int,
    var onItemClickListener: onItemClickListener,
) : BaseRvAdapter(dataList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaySourceViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_play_episode, parent, false)
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


            val layoutParams = holder.itemView.layoutParams
            when (orientation) {
                LinearLayoutManager.VERTICAL -> {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    if (layoutParams is ViewGroup.MarginLayoutParams) {
                        layoutParams.setMargins(8.dp, 4.dp, 8.dp, 4.dp)
                    }
                    holder.itemView.layoutParams = layoutParams
                }

                LinearLayoutManager.HORIZONTAL -> {
                    if (layoutParams is ViewGroup.MarginLayoutParams) {
                        layoutParams.setMargins(4.dp, 0, 4.dp, 0)
                    }
                    holder.itemView.layoutParams = layoutParams
                }
            }

            itemView.setOnClickListener {
                setSelectPosition(position)
                onItemClickListener.onItemClick(position)
            }
        }
    }

    fun setSelectPosition(position: Int) {
        notifyItemChanged(position)
        notifyItemChanged(currentPosition)
        currentPosition = position
    }
}