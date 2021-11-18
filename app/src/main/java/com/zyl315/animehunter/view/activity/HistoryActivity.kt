package com.zyl315.animehunter.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyl315.animehunter.databinding.ActivityHistoryBinding
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.WatchHistoryAdapter
import com.zyl315.animehunter.view.adapter.holder.WatchHistoryViewHolder
import com.zyl315.animehunter.viewmodel.activity.HistoryViewModel

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    lateinit var historyAdapter: WatchHistoryAdapter
    lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        historyAdapter = WatchHistoryAdapter(this, viewModel.watchHistoryList)

        initView()
        initData()
        observe()
    }

    private fun initView() {
        mBinding.run {
            rvHistoryList.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rvHistoryList.adapter = historyAdapter
        }
    }

    private fun initData() {
        viewModel.loadAllWatchHistory()
    }

    private fun observe() {
        viewModel.loadWatchHistorySuccess.observe(this) { success ->
            if (success) {
                historyAdapter.notifyDataSetChanged()
            } else {
                showToast(message = "加载观看历史失败")
            }
        }
    }

    override fun getBinding() = ActivityHistoryBinding.inflate(layoutInflater)
}