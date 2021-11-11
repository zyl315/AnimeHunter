package com.zyl315.animehunter.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.PlayStatus
import com.zyl315.animehunter.bean.age.BangumiBean
import com.zyl315.animehunter.bean.age.EpisodeBean
import com.zyl315.animehunter.databinding.ActivityPlayBinding
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.view.fragment.PlaySourceFragment
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel

class PlayActivity : BaseActivity<ActivityPlayBinding>() {
    private lateinit var viewModel: PlayViewModel
    private lateinit var mPlaySourceAdapter: PlaySourceAdapter
    private lateinit var playerView: PlayerView

    //    private lateinit var playerController: BasisVideoController
    private lateinit var player: ExoPlayer

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initListener()
        initPlayer()
        initObserve()
    }

    override fun onPause() {
        super.onPause()
//        player.pause()
//        playerView.pause()
    }

    override fun onResume() {
        super.onResume()

//        playerView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
//        playerView.release()
    }


    override fun onBackPressed() {
//        if (!playerView.onBackPressed()) super.onBackPressed()
        super.onBackPressed()
    }


    private fun initView() {
        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        viewModel.bangumi = intent.getSerializableExtra("bangumi") as BangumiBean

        mPlaySourceAdapter = PlaySourceAdapter(viewModel.playSource, object : onItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.getPlayUrl(position)
                player.stop()
//                playerView.release()
            }
        })

        mBinding.run {
            this@PlayActivity.playerView = playerView
            rvPlayUrlList.apply {
                layoutManager =
                    GridLayoutManager(this@PlayActivity, 3, RecyclerView.VERTICAL, false)
                adapter = mPlaySourceAdapter
            }
            tvName.text = viewModel.bangumi.name
            tvDescription.text = viewModel.bangumi.description
        }
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

            tvExpand.setOnClickListener {
                PlaySourceFragment().show(supportFragmentManager, "play_source")
            }
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
                    viewModel.currentEpisodeBean.url = ""
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
        player = ExoPlayer.Builder(this).build()
        mBinding.playerView.player = player
//        playerController = BasisVideoController(this)
//        playerView.setController(playerController)
    }


    private fun starPlay(episodeBean: EpisodeBean) {
//        playerController.setTitle(episodeBean.title)
//        playerView.url = episodeBean.url
//        playerView.start()
        player.setMediaItem(MediaItem.fromUri(episodeBean.url))
        player.prepare()
        player.play()
    }
}