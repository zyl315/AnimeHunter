package com.zyl315.animehunter.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyl315.animehunter.bean.age.HomeResultBean
import com.zyl315.animehunter.bean.age.WeeklyPlayListBean
import com.zyl315.animehunter.repository.datasource.AbstractDataSource
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.interfaces.RequestState
import kotlinx.coroutines.launch

class HomeFragmentViewModel : ViewModel() {
    val dataSource: AbstractDataSource = DataSourceManager.getDataSource()

    val homeContentState: MutableLiveData<RequestState<HomeResultBean>> = MutableLiveData()
    val weeklyPlayListState: MutableLiveData<RequestState<WeeklyPlayListBean>> = MutableLiveData()

    fun getHomeContent() {
        viewModelScope.launch {
            homeContentState.value = dataSource.getHomeContent()
        }
    }

    fun getWeekPlayList() {
        viewModelScope.launch {
            weeklyPlayListState.value = dataSource.getWeeklyPlayList()
        }
    }

}