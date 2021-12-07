package com.zyl315.animehunter.view.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivityHistoryBinding
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.view.adapter.WatchHistoryAdapter
import com.zyl315.animehunter.viewmodel.activity.HistoryViewModel

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    lateinit var historyAdapter: WatchHistoryAdapter
    lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        initView()
        observe()
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    private fun initView() {
        historyAdapter = WatchHistoryAdapter(this)
        viewModel.watchHistoryList.observe(this) { list -> historyAdapter.submitList(list) }

        mBinding.run {
            rvHistoryList.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rvHistoryList.adapter = historyAdapter

            titleBar.ibLeftIcon.setOnClickListener {
                finish()
            }
            titleBar.tvTitle.text = getString(R.string.watched_history)

            titleBar.tvRight.apply {
                text = getString(R.string.management)
                setOnClickListener {
                    viewModel.isSelectModel = !viewModel.isSelectModel
                    updateUI()
                }
            }

            rbSelectAll.setOnClickListener {
                if (!viewModel.isSelectAll()) {
                    rbSelectAll.isChecked = true
                    viewModel.addAll()
                } else {
                    rbSelectAll.isChecked = false
                    viewModel.clearAll()
                }
                updateUI()
            }

            tvDelete.setOnClickListener {
                if (viewModel.selectSet.size == 0) return@setOnClickListener

                AlertDialog.Builder(this@HistoryActivity)
                    .setMessage(R.string.clear_the_selected_history)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        viewModel.deleteHistory()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
    }

    private fun initData() {
        viewModel.loadAllWatchHistory()
    }


    private fun observe() {
        viewModel.loadWatchHistorySuccess.observe(this) { success ->
            if (!success) showToast(message = "加载观看历史失败")
        }


    }

    private fun updateUI() {
        mBinding.run {
            if (viewModel.isSelectModel) {
                titleBar.tvRight.text = getString(R.string.cancel)
                historyAdapter.enterSelectModel()
                rlBottomControlView.visible()
            } else {
                titleBar.tvRight.text = getString(R.string.management)
                viewModel.isSelectModel = false
                historyAdapter.exitSelectModel()
                rlBottomControlView.gone()
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isSelectModel) {
            viewModel.isSelectModel = false
            updateUI()
            return
        }
        super.onBackPressed()
    }

    override fun getBinding() = ActivityHistoryBinding.inflate(layoutInflater)
}