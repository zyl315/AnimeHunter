package com.zyl315.animehunter.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivityCatalogBinding
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSource
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.adapter.BangumiAdapter2
import com.zyl315.animehunter.ui.adapter.CatalogAdapter
import com.zyl315.animehunter.ui.adapter.EmptyAdapter
import com.zyl315.animehunter.ui.adapter.holder.ViewHolderType
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.viewmodel.activity.CatalogViewModel
import kotlin.math.abs

class CatalogActivity : BaseActivity<ActivityCatalogBinding>() {
    val viewModel: CatalogViewModel by viewModels<CatalogViewModel>()
    lateinit var webView: WebView
    lateinit var catalogAdapter: CatalogAdapter
    lateinit var bangumiAdapter: BangumiAdapter2
    lateinit var emptyAdapter: EmptyAdapter
    private var isInitialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initWebView()
        initListener()
        loadData(viewModel.catalogUrl)
    }

    override fun getBinding() = ActivityCatalogBinding.inflate(layoutInflater)


    private fun initView() {
        catalogAdapter = CatalogAdapter()
        viewModel.catalogList.observe(this) { list ->
            catalogAdapter.submitList(list.toList())
            mBinding.tvCatalogExpand.text = getCatalogText()
        }

        bangumiAdapter = BangumiAdapter2(this, ViewHolderType.BANGUMI_COVER_2)
        viewModel.mBangumiCoverList.observe(this) { list -> bangumiAdapter.submitList(list) }

        emptyAdapter = EmptyAdapter()

        mBinding.apply {
            titleBar.ibLeftIcon.setOnClickListener { finish() }
            titleBar.tvTitle.text = getString(R.string.catalog)
            titleBar.tvRight.visible()

            rvCatalog.layoutManager = LinearLayoutManager(this@CatalogActivity)
            rvCatalog.adapter = catalogAdapter
            rvBangumi.layoutManager = GridLayoutManager(this@CatalogActivity, 3)
            rvBangumi.adapter = bangumiAdapter
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView = mBinding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.blockNetworkImage = true
        webView.addJavascriptInterface(this, HTML_OUT)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                viewModel.catalogState.value = RequestState.Error()
            }
        }
    }

    private fun initListener() {
        catalogAdapter.onTabItemSelectedListener = object : CatalogAdapter.OnTabItemSelectedListener {
            override fun onTabItemSelected(catalogTagPosition: Int, tabItemPosition: Int) {
                if (mBinding.smartRefreshLayout.autoRefreshAnimationOnly()) {
                    mBinding.tvCatalogExpand.text = getCatalogText()
                    loadData(viewModel.getCatalogUrl(catalogTagPosition, tabItemPosition))
                } else {
                    showToast(resId = R.string.still_loading)
                }
            }
        }


        mBinding.run {
            smartRefreshLayout.setOnLoadMoreListener {
                viewModel.getMoreBangumi()
            }

            smartRefreshLayout.setOnRefreshListener {
                loadData(viewModel.catalogUrl)
            }

            tvCatalogExpand.setOnClickListener {
                appBarLayout.setExpanded(true)
                rvBangumi.scrollToPosition(0)
            }

            appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (abs(verticalOffset) >= appBarLayout.totalScrollRange && verticalOffset != 0 && tvCatalogExpand.text.toString()
                        .isNotEmpty()
                ) {
                    tvCatalogExpand.visible()
                } else {
                    tvCatalogExpand.gone()
                }
            })
        }

        viewModel.catalogState.observe(this) { state ->
            state.success {
                mBinding.run {
                    smartRefreshLayout.setNoMoreData(it.data.isLastPage)
                    smartRefreshLayout.finishRefresh(true)
                    if (!isInitialized) {
                        isInitialized = true
                        smartRefreshLayout.visible(true)
                        emptyView.hide()
                    }

                    if (it.data.bangumiCoverList.isEmpty()) {
                        rvBangumi.adapter = emptyAdapter
                        (rvBangumi.layoutManager as GridLayoutManager).spanCount = 1
                        smartRefreshLayout.setEnableLoadMore(false)
                    } else {
                        rvBangumi.adapter = bangumiAdapter
                        (rvBangumi.layoutManager as GridLayoutManager).spanCount = 3
                        bangumiAdapter.submitList(it.data.bangumiCoverList.toList())
                        smartRefreshLayout.setEnableLoadMore(true)
                    }
                }
            }

            state.error {
                mBinding.run {
                    if (isInitialized) {
                        smartRefreshLayout.finishRefresh(1000, false, null)
                    } else {
                        emptyView.show(
                            getString(R.string.loaded_failed), getString(R.string.retry_click)
                        ) {
                            loadData(viewModel.catalogUrl)
                            emptyView.show(true)
                        }
                    }
                }
            }
        }

        viewModel.bangumiState.observe(this) { state ->
            state.success {
                bangumiAdapter.submitList(it.data.bangumiCoverList.toList())
                mBinding.apply {
                    smartRefreshLayout.setNoMoreData(it.data.isLastPage)
                    smartRefreshLayout.finishLoadMore(true)
                    emptyView.hide()
                }
            }

            state.error {
                mBinding.smartRefreshLayout.finishLoadMore(false)
            }
        }

    }

    private fun loadData(url: String) {
        mBinding.smartRefreshLayout.setNoMoreData(false)
        if (viewModel.dataSource is AgeFansDataSource) {
            webView.loadUrl(url)
        } else {
            viewModel.getCatalog(viewModel.catalogUrl)
        }
    }

    private fun getCatalogText(): String {
        val selectedTagList = mutableListOf<String>()
        viewModel.catalogList.value?.forEach { tag ->
            tag.catalogItemBeanList.forEach { item ->
                if (item.isSelected && item.name != "全部") selectedTagList.add(item.name)
            }
        }
        return selectedTagList.joinToString(separator = "·")

    }

    @JavascriptInterface
    fun processHTML(html: String) {
        viewModel.getCatalog(html)
    }

    companion object {
        const val HTML_OUT = "HTMLOUT"
    }
}