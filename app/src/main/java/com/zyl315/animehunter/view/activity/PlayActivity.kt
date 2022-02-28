package com.zyl315.animehunter.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.execption.IPCheckException
import com.zyl315.animehunter.execption.UnSupportPlayTypeException
import com.zyl315.animehunter.util.BackHandlerHelper
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.view.fragment.PlaySourceFragment
import com.zyl315.animehunter.view.widget.BangumiVideoController
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel
import com.zyl315.player.player.AbstractPlayer
import com.zyl315.player.player.ProgressManager
import com.zyl315.player.player.VideoView
import com.zyl315.player.player.VideoViewManager

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    lateinit var viewModel: PlayViewModel
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
    }

    private fun initView() {
        mPlaySourceAdapter = PlaySourceAdapter(
            viewModel.getEpisodeList(viewModel.playSourceIndex),
            GridLayoutManager.HORIZONTAL,
            viewModel.playEpisodeIndex,
            object : onItemClickListener {
                override fun onItemClick(position: Int) {
                    viewModel.playSourceIndex = viewModel.selectSourceIndex
                    viewModel.getPlayUrl(position)
                }
            })

        mBinding.run {
            this@PlayActivity.playerView = playerView
            rvPlayUrlList.layoutManager =
                GridLayoutManager(this@PlayActivity, 1, GridLayoutManager.HORIZONTAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            rvPlayUrlList.setHasFixedSize(true)

            tvName.text = viewModel.bangumiBean.name
            tvDescription.text = viewModel.bangumiBean.description
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
    }

    private fun initData() {
        viewModel.bangumiBean = intent.getSerializableExtra(BANGUMI_BEAN) as BangumiBean
        viewModel.getWatchHistory()

        viewModel.playSourceState.observe(this) { state ->
            state.success {
                mBinding.tabPlaySource.apply {
                    removeAllTabs()
                    for (index in viewModel.playSourceList.indices) {
                        addTab(
                            newTab().setText("播放源${index + 1}"),
                            index == viewModel.playSourceIndex
                        )
                    }
                }
                if (viewModel.continuePlay) {
                    viewModel.getPlayUrl(viewModel.playEpisodeIndex)
                }
            }

            state.error {
                showToast(resId = R.string.get_play_source_failed)
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
            playerView.release()
        }
    }

    private fun initPlayer() {
        VideoViewManager.instance().setPlayOnMobileNetwork(false)
        playerController = BangumiVideoController(this)
        playerController.setEnableInNormal(true)
        playerView.setVideoController(playerController)
        playerView.setProgressManager(object : ProgressManager() {
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
        playerView.setUrl(episodeBean.url)
        playerView.start()
    }

    private fun openInBrowser() {
        Snackbar.make(
            mBinding.playerView,
            getString(R.string.unsupport_playback_links),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.confirm)) {
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
        }.show()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            playerView.release()
        }
        playerView.pause()
    }

    override fun onResume() {
        super.onResume()
        playerView.resume()
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            playerView.release()
        }
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
        const val BANGUMI_BEAN = "BANGUMI_BEAN"
    }
}