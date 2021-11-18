package com.zyl315.animehunter.view.adapter.holder

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import org.w3c.dom.Text

class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class CoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvNewName: TextView = itemView.findViewById(R.id.tv_newName)
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
    val tvBangumiType: TextView = itemView.findViewById(R.id.tv_bangumiType)
    val tvPremiereTime: TextView = itemView.findViewById(R.id.tv_premiereTime)
    val tvPlayStatus: TextView = itemView.findViewById(R.id.tv_playStatus)
    val tvPlotType: TextView = itemView.findViewById(R.id.tv_plotType)
}

class PlaySourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvEpisode: TextView = itemView.findViewById(R.id.tv_episode)
}

class WatchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
    val tvBangumiTitle: TextView = itemView.findViewById(R.id.tv_bangumiTitle)
    val tvEpisodeTitle: TextView = itemView.findViewById(R.id.tv_episodeTitle)
    val tvLastWatchTime: TextView = itemView.findViewById(R.id.tv_lastWatchTime)
}