package com.zyl315.animehunter.view.widget

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.zyl315.animehunter.R
import com.zyl315.player.controller.GestureVideoController
import com.zyl315.player.player.VideoView
import com.zyl315.player.util.PlayerUtils
import com.zyl315.ui.controller.component.*

class BangumiVideoController : GestureVideoController, View.OnClickListener {
    lateinit var lockButton: ImageView
    lateinit var loadingProgress: ProgressBar
    lateinit var speedTip: TextView

    private lateinit var prepareView: PrepareView
    private lateinit var errorView: ErrorView
    private lateinit var titleView: TitleView
    private lateinit var completeView: CompleteView
    private lateinit var bottomControlView: BottomControlView

    private var mPlaySpeed = 1f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getLayoutId(): Int {
        return R.layout.player_layout_bangumi_controller
    }

    override fun initView() {
        super.initView()
        lockButton = findViewById(R.id.lock)
        lockButton.setOnClickListener(this)
        loadingProgress = findViewById(R.id.loading)
        speedTip = findViewById(R.id.speedTip)

        completeView = CompleteView(context)
        errorView = ErrorView(context)
        prepareView = PrepareView(context)
        titleView = TitleView(context)
        bottomControlView = BottomControlView(context)
        addControlComponent(completeView, errorView, prepareView, titleView)
        addControlComponent(bottomControlView, GestureView(context))
    }

    fun setTitle(title: String) {
        titleView.setTitle(title)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.lock -> mControlWrapper.toggleLockState()
        }
    }

    override fun onLockStateChanged(isLocked: Boolean) {
        if (isLocked) {
            lockButton.isSelected = true
            Toast.makeText(context, R.string.dkplayer_locked, Toast.LENGTH_SHORT).show()
        } else {
            lockButton.isSelected = false
            Toast.makeText(context, R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onVisibilityChanged(isVisible: Boolean, anim: Animation?) {
        if (mControlWrapper.isFullScreen) {
            if (isVisible) {
                if (lockButton.visibility == GONE) {
                    lockButton.visibility = VISIBLE
                    if (anim != null) {
                        lockButton.startAnimation(anim)
                    }
                }
            } else {
                lockButton.visibility = GONE
                if (anim != null) {
                    lockButton.startAnimation(anim)
                }
            }
        }
    }

    override fun onPlayerStateChanged(playerState: Int) {
        super.onPlayerStateChanged(playerState)
        when (playerState) {
            VideoView.PLAYER_NORMAL -> {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                lockButton.visibility = GONE
            }
            VideoView.PLAYER_FULL_SCREEN -> if (isShowing) {
                lockButton.visibility = VISIBLE
            } else {
                lockButton.visibility = GONE
            }
        }
        if (mActivity != null && hasCutout()) {
            val orientation = mActivity!!.requestedOrientation
            val dp24 = PlayerUtils.dp2px(context, 24f)
            val cutoutHeight = cutoutHeight
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                val lblp = lockButton.layoutParams as LayoutParams
                lblp.setMargins(dp24, 0, dp24, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                val layoutParams = lockButton.layoutParams as LayoutParams
                layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0)
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                val layoutParams = lockButton.layoutParams as LayoutParams
                layoutParams.setMargins(dp24, 0, dp24, 0)
            }
        }
    }

    override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        when (playState) {
            VideoView.STATE_IDLE -> {
                lockButton.isSelected = false
                loadingProgress.visibility = GONE
            }

            VideoView.STATE_PLAYING,
            VideoView.STATE_PAUSED,
            VideoView.STATE_PREPARED,
            VideoView.STATE_ERROR,
            VideoView.STATE_BUFFERED -> {
                loadingProgress.visibility = GONE
            }

            VideoView.STATE_PREPARING,
            VideoView.STATE_BUFFERING -> {
                loadingProgress.visibility = VISIBLE
            }
            VideoView.STATE_PLAYBACK_COMPLETED -> {
                loadingProgress.visibility = GONE
                lockButton.visibility = GONE
                lockButton.isSelected = false
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (isLocked) {
            show()
            Toast.makeText(context, R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show()
            return true
        }
        return if (mControlWrapper.isFullScreen) {
            stopFullScreen()
        } else super.onBackPressed()
    }


    override fun onLongPress(e: MotionEvent?) {
        super.onLongPress(e)
        if (!mControlWrapper.isPlaying || bottomControlView.isShowEpisodes()) return

        speedTip.visibility = VISIBLE
        mPlaySpeed = mControlWrapper.speed
        mControlWrapper.speed = 2.0f
    }


    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        if (bottomControlView.isShowEpisodes()) {
            bottomControlView.hideEpisodes()
            return true
        }
        return super.onSingleTapConfirmed(e)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (speedTip.isVisible) {
                    speedTip.visibility = GONE
                    mControlWrapper.speed = mPlaySpeed
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}