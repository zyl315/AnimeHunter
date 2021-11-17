package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.PlayStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlaySourceBean
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.model.impls.agefans.PlayModel
import com.zyl315.animehunter.model.interfaces.IPlayModelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PlayViewModel : ViewModel() {
    private val playModel: IPlayModelModel by lazy {
        PlayModel()
    }

    var playSourceList: MutableList<PlaySourceBean> = mutableListOf()
    var playSource: MutableList<EpisodeBean> = mutableListOf()
    var currentSourceIndex = -1
    var currentPlayPosition = -1

    val playStatus: MutableLiveData<PlayStatus> = MutableLiveData()
    val ipBanned: MutableLiveData<Boolean> = MutableLiveData()
    val currentPlayTag: MutableLiveData<String> = MutableLiveData()

    lateinit var bangumi: BangumiBean
    lateinit var currentEpisodeBean: EpisodeBean


    fun getPlaySource() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playSourceList = playModel.getPlaySource(bangumi.bangumiID)
                playSourceList.forEachIndexed { index, playSourceBean ->
                    if (playSourceBean.select) setPlaySource(index)
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
                setPlayUrl(position)
                if (currentEpisodeBean.url == "") {
                    currentEpisodeBean.url = playModel.getPlayUrl(currentEpisodeBean.href)
                }
                playStatus.postValue(PlayStatus.GET_PLAY_URL_SUCCESS)
            } catch (e: IPCheckException) {
                ipBanned.postValue(true)
            } catch (e: Exception) {
                playStatus.postValue(PlayStatus.GET_PLAY_URL_FAILED)
                e.printStackTrace()
            }

        }
    }

    fun equalToCurrentPlayTag(): Boolean {
        return currentPlayTag.value == getCurrentPlayTag()
    }

    fun getCurrentPlayTag(): String {
        return "$currentSourceIndex$currentPlayPosition"
    }

    fun setPlaySource(index: Int) {
        if (index < 0 || index >= playSourceList.size || index == currentSourceIndex) return
        currentSourceIndex = index
        playSource.clear()
        playSource.addAll(playSourceList[index].episodeList)
    }

    fun setPlayUrl(position: Int) {
        currentEpisodeBean = playSource[position]
        currentPlayPosition = position
        currentPlayTag.postValue(getCurrentPlayTag())
    }
}