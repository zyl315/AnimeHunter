package com.zyl315.animehunter.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.Status
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.PlayUrlAdapter
import com.zyl315.animehunter.viewmodel.PlayViewModel

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    private lateinit var viewModel: PlayViewModel
    private lateinit var playUrlAdapter: PlayUrlAdapter

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        viewModel.bangumi = intent.getSerializableExtra("bangumi") as BangumiBean

        playUrlAdapter = PlayUrlAdapter(viewModel.episodeList)

        mBinding.run {
            rvPlayUrlList.apply {
                layoutManager =
                    GridLayoutManager(this@PlayActivity, 3, RecyclerView.VERTICAL, false)
                adapter = playUrlAdapter
            }
            tvName.text = viewModel.bangumi.name
            tvDescription.text = viewModel.bangumi.description
        }

        init()
        observe()
    }

    private fun observe() {
        viewModel.apply {
            resultStatus.observe(this@PlayActivity) {
                if (it == Status.Failed) {
                    showToast(resId = R.string.play_failed)
                }
            }

            playUrlList.observe(this@PlayActivity) {
                mBinding.tabPlayUrl.removeAllTabs()
                it.forEachIndexed { index, url ->
                    if (url.select) {
                        viewModel.episodeList.apply {
                            clear()
                            addAll(url.episodeList)
                        }
                        playUrlAdapter.notifyDataSetChanged()
                    }
                    mBinding.tabPlayUrl.apply {
                        addTab(newTab().apply { text = "播放源${index + 1}" }, url.select)
                    }
                }
            }
        }
    }


    private fun init() {
        viewModel.getDetailPage()
    }
}