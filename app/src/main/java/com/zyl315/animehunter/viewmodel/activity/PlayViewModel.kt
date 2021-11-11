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

    val playStatus: MutableLiveData<PlayStatus> = MutableLiveData()
    val ipBanned: MutableLiveData<Boolean> = MutableLiveData()

    var currentSelect = -1
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


    fun getPlayUrl(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentEpisodeBean = playSource[index]
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

    fun setPlaySource(index: Int) {
        if (index < 0 || index >= playSourceList.size || index == currentSelect) return
        currentSelect = index
        playSource.clear()
        playSource.addAll(playSourceList[currentSelect].episodeList)
    }
}