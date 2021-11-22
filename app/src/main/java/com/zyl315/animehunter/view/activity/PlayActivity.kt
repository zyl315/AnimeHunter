package com.zyl315.animehunter.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.Const
import com.zyl315.animehunter.api.PlayStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.util.BackHandlerHelper
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.view.fragment.PlaySourceFragment
import com.zyl315.animehunter.view.widget.BangumiVideoController
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel
import com.zyl315.player.player.AbstractPlayer
import com.zyl315.player.player.VideoView

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    private lateinit var viewModel: PlayViewModel
    private lateinit var mPlaySourceAdapter: PlaySourceAdapter
    private lateinit var playerView: VideoView<AbstractPlayer>

    private lateinit var playerController: BangumiVideoController

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)

        initData()
        initView()
        initListener()
        initPlayer()
        initObserve()
    }

    private fun initView() {
        mPlaySourceAdapter = PlaySourceAdapter(
            viewModel.playSource,
            GridLayoutManager.HORIZONTAL,
            viewModel.currentEpisodeIndex,
            object : onItemClickListener {
                override fun onItemClick(position: Int) {
                    viewModel.getPlayUrl(position)
                }
            })

        mBinding.run {
            this@PlayActivity.playerView = playerView
            rvPlayUrlList.layoutManager =
                GridLayoutManager(this@PlayActivity, 1, GridLayoutManager.HORIZONTAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            rvPlayUrlList.setHasFixedSize(true)

            tvName.text = viewModel.bangumi.name
            tvDescription.text = viewModel.bangumi.description
        }
    }

    private fun initListener() {
        mBinding.run {
            tabPlaySource.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        viewModel.setPlaySource(tab.position)
                        mPlaySourceAdapter.currentPosition =
                            if (viewModel.equalToCurrentPlayTag()) viewModel.currentEpisodeIndex else -1
                        mPlaySourceAdapter.notifyDataSetChanged()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            tvExpand.setOnClickListener {
                PlaySourceFragment(Gravity.BOTTOM).apply {
                    backgroundColorId = R.color.design_default_color_background
                }.show(supportFragmentManager, R.id.fragment_container)

            }
        }

        playerView.addOnStateChangeListener(object : VideoView.OnStateChangeListener {
            override fun onPlayerStateChanged(playerState: Int) {

            }

            override fun onPlayStateChanged(playState: Int) {
                when (playState) {
                    VideoView.STATE_IDLE,
                    VideoView.STATE_START_ABORT,
                    VideoView.STATE_PAUSED,
                    VideoView.STATE_PLAYBACK_COMPLETED -> {
                        viewModel.saveWatchHistory(playerView.currentPosition, playerView.duration)
                    }
                }
            }
        })
    }

    private fun initData() {
        viewModel.bangumi = intent.getSerializableExtra(BANGUMI_BEAN) as BangumiBean
        viewModel.getWatchHistory()
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
                                index == viewModel.currentSourceIndex
                            )
                        }
                    }
                    if (viewModel.continuePlay) {
                        viewModel.getPlayUrl(viewModel.currentEpisodeIndex)
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

        viewModel.currentPlayTag.observe(this) {
            if (viewModel.equalToCurrentPlayTag()) {
                mBinding.rvPlayUrlList.scrollToPosition(viewModel.currentEpisodeIndex)
                mPlaySourceAdapter.setSelectPosition(viewModel.currentEpisodeIndex)
            }
            playerView.release()
        }

        viewModel.unSupportPlayType.observe(this) {
            Snackbar.make(
                mBinding.playerView,
                getString(R.string.unsupport_playback_links),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.confirm)) {
                val uri = Uri.parse(viewModel.currentEpisodeBean.let {
                    return@let (Const.AgeFans.BASE_URL + it.href)
                })
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }.show()
        }
    }

    private fun initPlayer() {
        playerController = BangumiVideoController(this)
        playerView.setVideoController(playerController)
    }


    private fun starPlay(episodeBean: EpisodeBean) {
        playerController.setTitle(episodeBean.title)
        playerView.skipPositionWhenPlay(viewModel.getWatchHistoryPosition())
        playerView.setUrl(episodeBean.url)
        playerView.start()
    }

    override fun onPause() {
        super.onPause()
        playerView.pause()
    }

    override fun onResume() {
        super.onResume()
        playerView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerView.release()
    }


    override fun onBackPressed() {
        if (BackHandlerHelper.handleBackPress(this)) {
            return
        }
        if (playerView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        const val BANGUMI_BEAN = "BANGUMI_TAG"
    }
}