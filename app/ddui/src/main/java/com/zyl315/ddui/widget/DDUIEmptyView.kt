package com.zyl315.ddui.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zyl315.ddui.R


class DDUIEmptyView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    private  var mLoadView: ProgressBar
    private  var mTitleTextView: TextView
    private  var mDetailTextView: TextView
    private  var mButton: Button


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    init {
        LayoutInflater.from(context).inflate(R.layout.ddui_empty_view, this, true)
        mLoadView = findViewById(R.id.empty_view_loading)
        mTitleTextView = findViewById(R.id.empty_view_title)
        mDetailTextView = findViewById(R.id.empty_view_detail)
        mButton = findViewById(R.id.empty_view_button)

        val arr: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.DDUIEmptyView)
        val attrShowLoading = arr.getBoolean(R.styleable.DDUIEmptyView_ddui_show_loading, false)
        val attrTitleText = arr.getString(R.styleable.DDUIEmptyView_ddui_title_text)
        val attrDetailText = arr.getString(R.styleable.DDUIEmptyView_ddui_detail_text)
        val attrBtnText = arr.getString(R.styleable.DDUIEmptyView_ddui_btn_text)
        arr.recycle()
        show(attrShowLoading, attrTitleText, attrDetailText, attrBtnText)
    }


    fun show(
        loading: Boolean = false,
        titleText: String? = null,
        detailText: String? = null,
        btnText: String? = null,
        onButtonClickListener: OnClickListener? = null
    ) {
        visibility = VISIBLE
        setLoadingShowing(loading)
        setTitleText(titleText)
        setDetailText(detailText)
        setButton(btnText, onButtonClickListener)
    }

    fun show(loading: Boolean) {
        show(loading, null, null, null, null)
    }

    fun show(titleText: String, detailText: String) {
        show(false, titleText, detailText, null, null)
    }

    fun show(titleText: String, btnText: String, onClickListener: OnClickListener) {
        show(false, titleText, null, btnText, onClickListener)
    }

    fun hide() {
        visibility = GONE
        setLoadingShowing(false)
        setTitleText(null)
        setDetailText(null)
        setButton(null, null)
    }

    fun isShowing() = visibility == VISIBLE

    fun isLoading() = mLoadView.visibility == VISIBLE

    fun setLoadingShowing(show: Boolean) {
        mLoadView.visibility = if (show) VISIBLE else GONE
    }

    fun setTitleText(text: String?) {
        mTitleTextView.text = text
        mTitleTextView.visibility = if (text == null) GONE else VISIBLE
    }

    fun setDetailText(text: String?) {
        mDetailTextView.text = text
        mDetailTextView.visibility = if (text == null) GONE else VISIBLE
    }

    fun setTitleColor(color: Int) {
        mTitleTextView.setTextColor(color)
    }

    fun setDetailColor(color: Int) {
        mDetailTextView.setTextColor(color)
    }

    fun setButton(text: String?, onClickListener: OnClickListener?) {
        mButton.text = text
        mButton.visibility = if (text == null) GONE else VISIBLE
        mButton.setOnClickListener(onClickListener)
    }
}
