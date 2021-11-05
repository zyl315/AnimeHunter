package com.zyl315.animehunter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.api.Status
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.bean.age.PlayUrlBean
import com.zyl315.animehunter.model.impls.agefans.PlayModel
import com.zyl315.animehunter.model.interfaces.IPlayModelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PlayViewModel : ViewModel() {
    private val playModel: IPlayModelModel by lazy {
        PlayModel()
    }

    var resultStatus : MutableLiveData<Status> = MutableLiveData()
    var playUrlList : MutableLiveData<MutableList<PlayUrlBean>> = MutableLiveData()
    var episodeList: MutableList<EpisodeBean> = mutableListOf()

    lateinit var bangumi: BangumiBean


    fun getDetailPage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playModel.getDetailPage(bangumi.bangumiID).apply {
                    playUrlList.postValue(this)
                }
            } catch (e : Exception) {
                resultStatus.postValue(Status.Failed)
                e.printStackTrace()
            }
        }
    }


    fun getPlayUrl(bangumiId: String, playIndex: Int, epIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playModel.getPlayUrl(bangumiId, playIndex, epIndex)
                resultStatus.postValue(Status.Success)
            } catch (e: Exception) {
                resultStatus.postValue(Status.Failed)
                e.printStackTrace()
            }

        }
    }
}