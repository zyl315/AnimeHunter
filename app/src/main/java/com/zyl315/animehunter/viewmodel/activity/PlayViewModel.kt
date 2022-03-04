package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.BangumiDetailBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlayDetailResultBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.impls.HistoryRepository
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val historyRepository: IHistoryRepository = HistoryRepository()
    private val dataSource = DataSourceManager.getDataSource()

    var playSourceList: List<PlaySourceBean> = mutableListOf()

    var playSourceIndex = 0
    var playEpisodeIndex = 0
    var selectSourceIndex = 0

    var continuePlay = false

    val currentPlayTag: MutableLiveData<String> = MutableLiveData()

    val playDetailResultState: MutableLiveData<RequestState<PlayDetailResultBean>> =
        MutableLiveData()
    val playUrlState: MutableLiveData<RequestState<String>> = MutableLiveData()

    lateinit var bangumiDetailBean: BangumiDetailBean
    lateinit var currentEpisodeBean: EpisodeBean
    lateinit var watchHistory: WatchHistory


    fun getPlaySource(bangumiId: String) {
        viewModelScope.launch {
            playDetailResultState.value = dataSource.getPlaySource(bangumiId).apply {
                success {
                    bangumiDetailBean = it.data.bangumiDetailBean
                    playSourceList = it.data.playSourceBeanList
                    playSourceList.forEachIndexed { index, playSourceBean ->
                        if (!continuePlay) {
                            playSourceIndex = index
                        }
                    }
                }
            }
        }
    }

    fun getPlayUrl(position: Int) {
        viewModelScope.launch {
            playEpisodeIndex = position
            currentEpisodeBean = getEpisodeList(playSourceIndex)[playEpisodeIndex]
            currentPlayTag.value = getCurrentPlayTag()

            var result = playUrlState.value
            if (currentEpisodeBean.url.isBlank()) {
                result = dataSource.getPlayUrl(currentEpisodeBean.href).apply {
                    success {
                        currentEpisodeBean.url = it.data
                    }
                }
            }
            playUrlState.postValue(result)
        }
    }

    fun getWatchHistory(bangumiId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyRepository.loadWatchHistoryById(bangumiId)?.let {
                    watchHistory = it
                    playSourceIndex = watchHistory.dataSourceIndex
                    playEpisodeIndex = watchHistory.episodeIndex
                    continuePlay = true
                }
                getPlaySource(bangumiId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    fun isCurrentPlaySource(): Boolean {
        return selectSourceIndex == playSourceIndex
    }

    private fun getCurrentPlayTag(): String {
        return "$playSourceIndex$playEpisodeIndex"
    }

    fun saveWatchProgress(url: String?, currentPosition: Long, duration: Long) {
        if (!this::bangumiDetailBean.isInitialized || !this::currentEpisodeBean.isInitialized || duration == 0L) {
            return
        }
        if (url != currentEpisodeBean.url) {
            return
        }
        try {
            viewModelScope.launch(Dispatchers.IO) {
                historyRepository.saveWatchHistory(
                    WatchHistory(
                        bangumiDetailBean.bangumiID,
                        System.currentTimeMillis(),
                        playSourceIndex,
                        currentEpisodeBean.title,
                        playEpisodeIndex,
                        duration,
                        currentPosition,
                        bangumiDetailBean
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun playNextEpisode() {
        if (playEpisodeIndex < getEpisodeList(playSourceIndex).size - 1) {
            getPlayUrl(playEpisodeIndex + 1)
        }
    }

    fun getEpisodeList(sourceIndex: Int): MutableList<EpisodeBean> {
        return if (sourceIndex < playSourceList.size) {
            playSourceList[sourceIndex].episodeList
        } else {
            mutableListOf()
        }
    }

    fun getSourceHost() = dataSource.getHost()
}