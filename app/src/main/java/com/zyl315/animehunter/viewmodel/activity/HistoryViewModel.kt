package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.model.impls.HistoryModel
import com.zyl315.animehunter.model.interfaces.IHistoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val historyModel: IHistoryModel by lazy {
        HistoryModel()
    }

    val watchHistoryList: MutableList<WatchHistory> = mutableListOf()

    val loadWatchHistorySuccess: MutableLiveData<Boolean> = MutableLiveData()

    fun loadAllWatchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                watchHistoryList.apply {
                    clear()
                    addAll(historyModel.loadAllWatchHistory())
                }
                loadWatchHistorySuccess.postValue(true)
            } catch (e: Exception) {
                loadWatchHistorySuccess.postValue(false)
                e.printStackTrace()
            }
        }
    }
}