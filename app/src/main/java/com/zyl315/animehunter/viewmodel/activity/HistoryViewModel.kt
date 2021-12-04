package com.zyl315.animehunter.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.database.enity.WatchHistory
import com.zyl315.animehunter.repository.impls.HistoryRepository
import com.zyl315.animehunter.repository.interfaces.IHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val historyRepository: IHistoryRepository by lazy {
        HistoryRepository()
    }

    var isSelectModel = false
    val selectSet: MutableSet<WatchHistory> = mutableSetOf()

    var watchHistoryList: MutableLiveData<MutableList<WatchHistory>> = MutableLiveData()

    val loadWatchHistorySuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isSelectedAll: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadAllWatchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                watchHistoryList.postValue(historyRepository.loadAllWatchHistory())
            } catch (e: Exception) {
                loadWatchHistorySuccess.postValue(false)
                e.printStackTrace()
            }
        }
    }

    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                watchHistoryList.value.let {
                    historyRepository.deleteWatchHistory(*selectSet.toTypedArray())
                    loadAllWatchHistory()
                    selectSet.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addAll() {
        watchHistoryList.value?.let { selectSet.addAll(it) }
    }

    fun clearAll() {
        selectSet.clear()
    }

    fun isSelectAll(): Boolean {
        return selectSet.size == watchHistoryList.value?.size
    }
}