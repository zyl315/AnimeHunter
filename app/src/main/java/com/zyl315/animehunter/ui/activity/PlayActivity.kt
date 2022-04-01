package com.zyl315.animehunter.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.ui.adapter.PlaySourceAdapter
import com.zyl315.animehunter.ui.adapter.interfaces.OnItemClickListener
import com.zyl315.animehunter.ui.fragment.PlaySourceFragment
import com.zyl315.animehunter.ui.widget.BangumiVideoController
import com.zyl315.animehunter.util.BackHandlerHelper
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel
import com.zyl315.player.player.ProgressManager
import com.zyl315.player.player.VideoView
import com.zyl315.player.player.VideoViewManager

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    val viewModel: PlayViewModel by viewModels()
    private lateinit var mPlaySourceAdapter: PlaySourceAdapter

    private lateinit var playerController: BangumiVideoController

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bangumiId = intent.getStringExtra(BANGUMI_ID)!!

        initView()
        initListener()
        initPlayer()
        loadData()
    }

    private fun initView() {
        mPlaySourceAdapter =
            PlaySourceAdapter(GridLayoutManager.HORIZONTAL, viewModel.playEpisodeIndex)

        mPlaySourceAdapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.playSourceIndex = viewModel.selectSourceIndex
                viewModel.getPlayUrl(position)
            }
        }

        mBinding.run {
            rvPlayUrlList.layoutManager =
                GridLayoutManager(this@PlayActivity, 1, GridLayoutManager.HORIZONTAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            rvPlayUrlList.setHasFixedSize(true)
        }
    }

    private fun initListener() {
        mBinding.run {
            tabPlaySource.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        viewModel.selectSourceIndex = tab.position
                        mPlaySourceAdapter.updateList(
                            viewModel.playSourceList[tab.position].episodeList,
                            if (viewModel.isCurrentPlaySource()) viewModel.playEpisodeIndex else -1
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            tvExpand.setOnClickListener {
                val playSourceFragment =
                    PlaySourceFragment(Gravity.BOTTOM)
                playSourceFragment.backgroundColorId = R.color.card_background
                playSourceFragment.show(supportFragmentManager, R.id.fragment_container)
            }

            playerView.setOnStateChangeListener(object : VideoView.OnStateChangeListener {
                override fun onPlayStateChanged(playState: Int) {
                    if (playState == VideoView.STATE_PLAYBACK_COMPLETED) {
                        viewModel.playNextEpisode()
                    }
                }

                override fun onPlayerStateChanged(playerState: Int) {
                    if (playerState == VideoView.PLAYER_FULL_SCREEN) {
                        viewModel.selectSourceIndex = viewModel.playSourceIndex
                    }
                }
            })
        }

        viewModel.playDetailResultState.observe(this) { state ->
            state.success {
                mBinding.tvName.text = viewModel.bangumiDetailBean.name
                mBinding.tvDescription.text = viewModel.bangumiDetailBean.description
                mBinding.tabPlaySource.apply {
                    removeAllTabs()
                    for (index in viewModel.playSourceList.indices) {
                        addTab(
                            newTab().setText("播放源${index + 1}"),
                            index == viewModel.playSourceIndex
                        )
                    }
                }
                mBinding.dduiEmptyView.hide()
                mBinding.clDataArea.visible(true)
                if (viewModel.continuePlay) {
                    viewModel.getPlayUrl(viewModel.playEpisodeIndex)
                }
            }

            state.error {
                mBinding.dduiEmptyView.show(
                    getString(R.string.get_play_source_failed),
                    getString(R.string.retry_click)
                ) {
                    mBinding.dduiEmptyView.show(true)
                    loadData()
                }
            }
        }

        viewModel.playUrlState.observe(this) { state ->
            state.success {
                starPlay(viewModel.currentEpisodeBean)
            }

            state.error {
                when (it.throwable) {
                    is IPCheckException -> showToast(message = "请求过于频繁")
                    is UnSupportPlayTypeException -> openInBrowser()
                    else -> showToast(resId = R.string.get_play_url_failed)
                }
            }
        }

        viewModel.currentPlayTag.observe(this) {
            if (viewModel.isCurrentPlaySource()) {
                mBinding.rvPlayUrlList.scrollToPosition(viewModel.playEpisodeIndex)
                mPlaySourceAdapter.setSelectPosition(viewModel.playEpisodeIndex)
            }
            mBinding.playerView.release()
        }
    }

    private fun loadData() {
        viewModel.getPlaySource(viewModel.bangumiId)
    }

    private fun initPlayer() {
        VideoViewManager.instance().setPlayOnMobileNetwork(false)
        playerController = BangumiVideoController(this)
        playerController.setEnableInNormal(true)
        mBinding.playerView.setVideoController(playerController)
        mBinding.playerView.setProgressManager(object : ProgressManager() {
            override fun saveProgress(url: String?, progress: Long, duration: Long) {
                viewModel.saveWatchProgress(url, progress, duration)
            }

            override fun getSavedProgress(url: String?): Long {
                return viewModel.getWatchHistoryProgress()
            }
        })
    }


    private fun starPlay(episodeBean: EpisodeBean) {
        playerController.setTitle(episodeBean.title)
        mBinding.playerView.setUrl(episodeBean.url)
        mBinding.playerView.start()
    }

    private fun openInBrowser() {
        val snackbar = Snackbar.make(
            mBinding.playerView,
            getString(R.string.unsupport_playback_links),
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(getString(R.string.confirm)) {
            val uri = Uri.parse(viewModel.currentEpisodeBean.let {
                return@let (viewModel.getSourceHost() + it.href)
            })
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (packageManager.resolveActivity(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                ) != null
            ) {
                startActivity(intent)
            }
        }
        snackbar.show()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            mBinding.playerView.release()
        }
        mBinding.playerView.pause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.playerView.resume()
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            mBinding.playerView.release()
        }
    }

    override fun onBackPressed() {
        if (BackHandlerHelper.handleBackPress(this)) {
            return
        }
        if (mBinding.playerView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        const val BANGUMI_ID = "BANGUMI_ID"
    }
}