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

    var isSelectModel = false
    val selectSet: MutableSet<WatchHistory> = mutableSetOf()

    val watchHistoryList: MutableList<WatchHistory> = mutableListOf()

    val loadWatchHistorySuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isSelectedAll: MutableLiveData<Boolean> = MutableLiveData(false)
    val deleteHistorySuccess: MutableLiveData<Boolean> = MutableLiveData()

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

    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyModel.deleteWatchHistory(*selectSet.toTypedArray())
                watchHistoryList.removeAll(selectSet)
                deleteHistorySuccess.postValue(true)
            } catch (e: Exception) {
                deleteHistorySuccess.postValue(false)
                e.printStackTrace()
            }
        }
    }

    fun isSelectAll(): Boolean {
        return selectSet.size == watchHistoryList.size
    }
}