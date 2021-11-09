package com.zyl315.animehunter.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.yc.kernel.inter.AbstractVideoPlayer
import com.yc.video.controller.BaseVideoController
import com.yc.video.player.VideoPlayer
import com.yc.video.ui.view.BasisVideoController
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.PlayStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.viewmodel.PlayViewModel

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    private lateinit var viewModel: PlayViewModel
    private lateinit var mPlaySourceAdapter: PlaySourceAdapter
    private lateinit var player: VideoPlayer<AbstractVideoPlayer>
    private lateinit var playerController: BasisVideoController

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initListener()
        initObserve()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }


    override fun onBackPressed() {
        if (!player.onBackPressed()) super.onBackPressed()
    }


    private fun initView() {
        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        viewModel.bangumi = intent.getSerializableExtra("bangumi") as BangumiBean

        mPlaySourceAdapter = PlaySourceAdapter(viewModel.playSource, object : onItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.getPlayUrl(position)
            }
        })

        mBinding.run {
            player = playerView
            rvPlayUrlList.apply {
                layoutManager =
                    GridLayoutManager(this@PlayActivity, 3, RecyclerView.VERTICAL, false)
                adapter = mPlaySourceAdapter
            }
            tvName.text = viewModel.bangumi.name
            tvDescription.text = viewModel.bangumi.description
        }

        initPlayer()
    }

    private fun initListener() {
        mBinding.run {
            tabPlaySource.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab == null) return
                    viewModel.setPlaySource(tab.position)
                    mPlaySourceAdapter.updateDataList()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }

    private fun initData() {
        viewModel.getPlaySource()
    }

    private fun initObserve() {
        viewModel.playStatus.observe(this) {
            when (it) {
                PlayStatus.GET_PLAY_SOURCE_SUCCESS -> {
                    mBinding.tabPlaySource.apply {
                        removeAllTabs()
                        for (index in viewModel.playSourceList.indices) {
                            addTab(
                                newTab().setText("播放源${index + 1}"),
                                index == viewModel.currentSelect
                            )
                        }
                    }
                }

                PlayStatus.GET_PLAY_SOURCE_FAILED -> {
                    showToast(resId = R.string.get_play_source_failed)
                }

                PlayStatus.GET_PLAY_URL_SUCCESS -> {
                    starPlay(viewModel.currentEpisodeBean)
                }

                PlayStatus.GET_PLAY_URL_FAILED -> {
                    showToast(resId = R.string.get_play_url_failed)
                }
                else -> {
                }
            }
        }

        viewModel.ipBanned.observe(this) {
            if (it) showToast(message = "请求过于频繁")
        }
    }

    private fun initPlayer() {
        playerController = BasisVideoController(this)
        player.setController(playerController)
    }


    private fun starPlay(episodeBean: EpisodeBean) {
        playerController.setTitle(episodeBean.title)
        player.apply {
            url = episodeBean.originUrl
            if (isPlaying) replay(true) else start()
        }
    }
}