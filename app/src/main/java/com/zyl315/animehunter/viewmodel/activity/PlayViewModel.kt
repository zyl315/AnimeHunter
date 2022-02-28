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
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository
import com.zyl315.animehunter.repository.interfaces.ISourceRepository
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val historyRepository: IHistoryRepository = HistoryRepository()
    private val sourceRepository: ISourceRepository = AgeFansRepository()

    var playSourceList: List<PlaySourceBean> = mutableListOf()

    var playSourceIndex = 0
    var playEpisodeIndex = 0
    var selectSourceIndex = 0

    var continuePlay = false

    val currentPlayTag: MutableLiveData<String> = MutableLiveData()

    val playSourceState: MutableLiveData<RequestState<List<PlaySourceBean>>> = MutableLiveData()
    val playUrlState: MutableLiveData<RequestState<String>> = MutableLiveData()

    lateinit var bangumiBean: BangumiBean
    lateinit var currentEpisodeBean: EpisodeBean
    lateinit var watchHistory: WatchHistory

    fun getPlaySource() {
        viewModelScope.launch {
            playSourceState.value = sourceRepository.getPlaySource(bangumiBean.bangumiID).apply {
                success {
                    playSourceList = it.data
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
                historyRepository.loadWatchHistoryById(bangumiBean.bangumiID)?.let {
                    watchHistory = it
                    playSourceIndex = watchHistory.dataSourceIndex
                    playEpisodeIndex = watchHistory.episodeIndex
                    continuePlay = true
                }
                getPlaySource()
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
        if (!this::bangumiBean.isInitialized || !this::currentEpisodeBean.isInitialized || duration == 0L) {
            return
        }
        if (url != currentEpisodeBean.url) {
            return
        }
        try {
            viewModelScope.launch(Dispatchers.IO) {
                historyRepository.saveWatchHistory(
                    WatchHistory(
                        bangumiBean.bangumiID,
                        System.currentTimeMillis(),
                        playSourceIndex,
                        currentEpisodeBean.title,
                        playEpisodeIndex,
                        duration,
                        currentPosition,
                        bangumiBean
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun playNextEpisode() {
        if (playEpisodeIndex < getEpisodeList(playSourceIndex).size - 1 ) {
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

    fun getSourceHost() = sourceRepository.host
}