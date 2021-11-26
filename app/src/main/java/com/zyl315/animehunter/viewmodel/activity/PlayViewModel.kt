package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.PlayStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.repository.impls.HistoryRepository
import com.zyl315.animehunter.repository.impls.agefans.PlayRepository
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository
import com.zyl315.animehunter.repository.interfaces.IPlayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val playRepository: IPlayRepository = PlayRepository()
    private val historyModel: IHistoryRepository = HistoryRepository()

    var playSourceList: MutableList<PlaySourceBean> = mutableListOf()
    var playSource: MutableList<EpisodeBean> = mutableListOf()
    var currentSourceIndex = 1
    var currentEpisodeIndex = 1
    var continuePlay = false

    val playStatus: MutableLiveData<PlayStatus> = MutableLiveData()
    val ipBanned: MutableLiveData<Boolean> = MutableLiveData()
    val unSupportPlayType: MutableLiveData<Boolean> = MutableLiveData()
    val currentPlayTag: MutableLiveData<String> = MutableLiveData()

    lateinit var bangumi: BangumiBean
    lateinit var currentEpisodeBean: EpisodeBean
    lateinit var watchHistory: WatchHistory


    fun getPlaySource() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playSourceList = playRepository.getPlaySource(bangumi.bangumiID)
                playSourceList.forEachIndexed { index, playSourceBean ->
                    if (continuePlay) {
                        setPlaySource(currentSourceIndex)
                    } else if (playSourceBean.select) {
                        setPlaySource(index)
                    }
                }
                playStatus.postValue(PlayStatus.GET_PLAY_SOURCE_SUCCESS)
            } catch (e: Exception) {
                playStatus.postValue(PlayStatus.GET_PLAY_SOURCE_FAILED)
                e.printStackTrace()
            }
        }
    }


    fun getPlayUrl(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setPlayEpisode(position)
                if (currentEpisodeBean.url == "") {
                    currentEpisodeBean.url = playRepository.getPlayUrl(currentEpisodeBean.href)
                }
                playStatus.postValue(PlayStatus.GET_PLAY_URL_SUCCESS)
            } catch (e: UnSupportPlayTypeException) {
                unSupportPlayType.postValue(true)
            } catch (e: IPCheckException) {
                ipBanned.postValue(true)
            } catch (e: Exception) {
                playStatus.postValue(PlayStatus.GET_PLAY_URL_FAILED)
                e.printStackTrace()
            }
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
        currentEpisodeBean = playSource[position]
        currentEpisodeIndex = position
        currentPlayTag.postValue(getCurrentPlayTag())
    }

    fun saveWatchProgress(url: String?, currentPosition: Long, duration: Long) {
        if (!this::bangumi.isInitialized || !this::currentEpisodeBean.isInitialized || duration == 0L) {
            return
        }
        if(url != currentEpisodeBean.url) {
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