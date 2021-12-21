package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.repository.impls.HistoryRepository
import com.zyl315.animehunter.repository.impls.agefans.AgeFansRepository
import com.zyl315.animehunter.repository.impls.agefans.PlayRepository
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository
import com.zyl315.animehunter.repository.interfaces.IPlayRepository
import com.zyl315.animehunter.repository.interfaces.ISourceRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val historyModel: IHistoryRepository = HistoryRepository()
    private val sourceRepository: ISourceRepository = AgeFansRepository()

    var playSourceList: List<PlaySourceBean> = mutableListOf()
    var playSource: MutableList<EpisodeBean> = mutableListOf()
    var currentSourceIndex = 1
    var currentEpisodeIndex = 1
    var continuePlay = false

    val currentPlayTag: MutableLiveData<String> = MutableLiveData()

    val playSourceState: MutableLiveData<RequestState<List<PlaySourceBean>>> = MutableLiveData()
    val playUrlState: MutableLiveData<RequestState<String>> = MutableLiveData()

    lateinit var bangumi: BangumiBean
    lateinit var currentEpisodeBean: EpisodeBean
    lateinit var watchHistory: WatchHistory

    fun getPlaySource() {
        viewModelScope.launch {
            playSourceState.value = sourceRepository.getPlaySource(bangumi.bangumiID).apply {
                success {
                    playSourceList = it.data
                    playSourceList.forEachIndexed { index, playSourceBean ->
                        if (continuePlay) {
                            setPlaySource(currentSourceIndex)
                        } else if (playSourceBean.select) {
                            setPlaySource(index)
                        }
                    }
                }
            }
        }
    }

    fun getPlayUrl(position: Int) {
        viewModelScope.launch {
            setPlayEpisode(position)
            var result = playUrlState.value
            if (currentEpisodeBean.url.isBlank()) {
                result = sourceRepository.getPlayUrl(currentEpisodeBean.href).apply {
                    success {
                        currentEpisodeBean.url = it.data
                    }
                }
            }
            playUrlState.postValue(result)
        }
    }

    fun getWatchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyModel.loadWatchHistoryById(bangumi.bangumiID)?.let {
                    watchHistory = it
                    currentSourceIndex = watchHistory.dataSourceIndex
                    currentEpisodeIndex = watchHistory.episodeIndex
                    continuePlay = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getPlaySource()
        }
    }

    fun getWatchHistoryProgress(): Long {
        return if (!this::watchHistory.isInitialized || !continuePlay) {
            0
        } else {
            val position = watchHistory.watchedPosition
            watchHistory.watchedPosition = 0
            continuePlay = false
            position
        }
    }

    fun equalToCurrentPlayTag(): Boolean {
        return currentPlayTag.value == getCurrentPlayTag()
    }

    private fun getCurrentPlayTag(): String {
        return "$currentSourceIndex$currentEpisodeIndex"
    }

    fun setPlaySource(index: Int) {
        if (index < 0 || index >= playSourceList.size) return
        currentSourceIndex = index
        playSource.clear()
        playSource.addAll(playSourceList[index].episodeList)
    }

    private fun setPlayEpisode(position: Int) {
        if (position < 0 || position > playSource.size) return
        currentEpisodeBean = playSource[position]
        currentEpisodeIndex = position
        currentPlayTag.postValue(getCurrentPlayTag())
    }

    fun saveWatchProgress(url: String?, currentPosition: Long, duration: Long) {
        if (!this::bangumi.isInitialized || !this::currentEpisodeBean.isInitialized || duration == 0L) {
            return
        }
        if (url != currentEpisodeBean.url) {
            return
        }
        try {
            viewModelScope.launch(Dispatchers.IO) {
                historyModel.saveWatchHistory(
                    WatchHistory(
                        bangumi.bangumiID,
                        System.currentTimeMillis(),
                        currentSourceIndex,
                        currentEpisodeBean.title,
                        currentEpisodeIndex,
                        duration,
                        currentPosition,
                        bangumi
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}