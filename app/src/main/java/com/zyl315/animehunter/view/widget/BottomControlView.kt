package com.zyl315.animehunter.view.widget

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import com.zyl315.player.controller.ControlWrapper
import com.zyl315.player.controller.IControlComponent
import com.zyl315.player.player.VideoView
import com.zyl315.player.util.PlayerUtils

import com.zyl315.animehunter.R
import com.zyl315.animehunter.view.activity.PlayActivity
import com.zyl315.animehunter.view.fragment.PlaySourceFragment


/**
 * 底部控制栏
 */
class BottomControlView : FrameLayout, IControlComponent, View.OnClickListener,
    OnSeekBarChangeListener {
    private lateinit var mControlWrapper: ControlWrapper
    private var mTotalTime: TextView
    private var mCurrTime: TextView
    private var mFullScreen: ImageView
    private var mBottomContainer: LinearLayout
    private var mVideoProgress: SeekBar
    private var mBottomProgress: ProgressBar
    private var mPlayButton: ImageView
    private var mSelections: TextView
    private var mIsDragging = false
    private var mIsShowBottomProgress = true

    lateinit var playSourceFragment: PlaySourceFragment

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val layoutId: Int
        get() = R.layout.player_layout_bottom_control_view


    /**
     * 是否显示底部进度条，默认显示
     */
    fun showBottomProgress(isShow: Boolean) {
        mIsShowBottomProgress = isShow
    }

    override fun attach(controlWrapper: ControlWrapper) {
        mControlWrapper = controlWrapper
    }

    override fun getView(): View {
        return this
    }

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (isVisible) {
            mBottomContainer.visibility = VISIBLE
            if (anim != null) {
                mBottomContainer.startAnimation(anim)
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.visibility = GONE
            }
        } else {
            mBottomContainer.visibility = GONE
            if (anim != null) {
                mBottomContainer.startAnimation(anim)
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.visibility = VISIBLE
                val animation = AlphaAnimation(0f, 1f)
                animation.duration = 300
                mBottomProgress.startAnimation(animation)
            }
        }
    }

    override fun onPlayStateChanged(playState: Int) {
        when (playState) {
            VideoView.STATE_IDLE, VideoView.STATE_PLAYBACK_COMPLETED -> {
                visibility = GONE
                mBottomProgress.progress = 0
                mBottomProgress.secondaryProgress = 0
                mVideoProgress.progress = 0
                mVideoProgress.secondaryProgress = 0
            }
            VideoView.STATE_START_ABORT, VideoView.STATE_PREPARING, VideoView.STATE_PREPARED, VideoView.STATE_ERROR -> visibility =
                GONE
            VideoView.STATE_PLAYING -> {
                mPlayButton.isSelected = true
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing) {
                        mBottomProgress.visibility = GONE
                        mBottomContainer.visibility = VISIBLE
                    } else {
                        mBottomContainer.visibility = GONE
                        mBottomProgress.visibility = VISIBLE
                    }
                } else {
                    mBottomContainer.visibility = GONE
                }
                visibility = VISIBLE
                //开始刷新进度
                mControlWrapper.startProgress()
            }
            VideoView.STATE_PAUSED -> mPlayButton.isSelected = false
            VideoView.STATE_BUFFERING -> {
                mControlWrapper.stopProgress()
                mPlayButton.isSelected = mControlWrapper.isPlaying
            }
            VideoView.STATE_BUFFERED -> {
                mControlWrapper.startProgress()
                mPlayButton.isSelected = mControlWrapper.isPlaying
            }
        }
    }

    override fun onPlayerStateChanged(playerState: Int) {
        when (playerState) {
            VideoView.PLAYER_NORMAL -> {
                mFullScreen.isSelected = false
                mSelections.visibility = GONE
            }
            VideoView.PLAYER_FULL_SCREEN -> {
                mFullScreen.isSelected = true
                mSelections.visibility = VISIBLE
            }
        }
        val activity = PlayerUtils.scanForActivity(context)
        if (activity != null && mControlWrapper.hasCutout()) {
            val orientation = activity.requestedOrientation
            val cutoutHeight = mControlWrapper.cutoutHeight
            when (orientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    mBottomContainer.setPadding(0, 0, 0, 0)
                    mBottomProgress.setPadding(0, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    mBottomContainer.setPadding(cutoutHeight, 0, 0, 0)
                    mBottomProgress.setPadding(cutoutHeight, 0, 0, 0)
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    mBottomContainer.setPadding(0, 0, cutoutHeight, 0)
                    mBottomProgress.setPadding(0, 0, cutoutHeight, 0)
                }
                else -> {

                }
            }
        }
    }

    override fun setProgress(duration: Int, position: Int) {
        if (mIsDragging) {
            return
        }
        if (duration > 0) {
            mVideoProgress.isEnabled = true
            val pos = (position * 1.0 / duration * mVideoProgress.max).toInt()
            mVideoProgress.progress = pos
            mBottomProgress.progress = pos
        } else {
            mVideoProgress.isEnabled = false
        }
        val percent = mControlWrapper.bufferedPercentage
        if (percent >= 95) { //解决缓冲进度不能100%问题
            mVideoProgress.secondaryProgress = mVideoProgress.max
            mBottomProgress.secondaryProgress = mBottomProgress.max
        } else {
            mVideoProgress.secondaryProgress = percent * 10
            mBottomProgress.secondaryProgress = percent * 10
        }
        mTotalTime.text = PlayerUtils.stringForTime(duration)
        mCurrTime.text = PlayerUtils.stringForTime(position)
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        onVisibilityChanged(!isLocked, null)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fullscreen -> {
                toggleFullScreen()
            }
            R.id.iv_play -> {
                mControlWrapper.togglePlay()
            }
            R.id.selections -> {
                showEpisodes()
            }
        }
    }

    /**
     * 横竖屏切换
     */
    private fun toggleFullScreen() {
        val activity = PlayerUtils.scanForActivity(context)
        mControlWrapper.toggleFullScreen(activity)
        // 下面方法会根据适配宽高决定是否旋转屏幕
//        mControlWrapper.toggleFullScreenByVideoSize(activity);
    }

    fun isShowEpisodes(): Boolean {
        return this::playSourceFragment.isInitialized
                && playSourceFragment.isShow
    }

    private fun showEpisodes() {
        mControlWrapper.hide()
        val activity = PlayerUtils.scanForActivity(context)
        if (activity is PlayActivity) {
            playSourceFragment = PlaySourceFragment(Gravity.END).apply {
                backgroundColorId = R.color.background_black_70
                showCloseIcon = false
            }
            playSourceFragment.show(
                activity.supportFragmentManager,
                R.id.selections_container
            )
        }
    }

    fun hideEpisodes() {
        if (this::playSourceFragment.isInitialized) {
            playSourceFragment.dismiss()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mIsDragging = true
        mControlWrapper.stopProgress()
        mControlWrapper.stopFadeOut()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        val duration = mControlWrapper.duration
        val newPosition = duration * seekBar.progress / mVideoProgress.max
        mControlWrapper.seekTo(newPosition)
        mIsDragging = false
        mControlWrapper.startProgress()
        mControlWrapper.startFadeOut()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }
        val duration = mControlWrapper.duration
        val newPosition = duration * progress / mVideoProgress.max
        mCurrTime.text = PlayerUtils.stringForTime(newPosition.toInt())
    }

    init {
        visibility = GONE
        LayoutInflater.from(context).inflate(layoutId, this, true)
        mFullScreen = findViewById(R.id.fullscreen)
        mFullScreen.setOnClickListener(this)
        mBottomContainer = findViewById(R.id.bottom_container)
        mVideoProgress = findViewById(R.id.seekBar)
        mVideoProgress.setOnSeekBarChangeListener(this)
        mTotalTime = findViewById(R.id.total_time)
        mCurrTime = findViewById(R.id.curr_time)
        mPlayButton = findViewById(R.id.iv_play)
        mPlayButton.setOnClickListener(this)
        mBottomProgress = findViewById(R.id.bottom_progress)
        mSelections = findViewById(R.id.selections)
        mSelections.setOnClickListener(this)

        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mVideoProgress.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}