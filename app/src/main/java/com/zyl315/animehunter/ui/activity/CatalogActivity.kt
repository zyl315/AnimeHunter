package com.zyl315.animehunter.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivityCatalogBinding
import com.zyl315.animehunter.repository.interfaces.RequestState
import com.zyl315.animehunter.ui.adapter.BangumiAdapter2
import com.zyl315.animehunter.ui.adapter.CatalogAdapter
import com.zyl315.animehunter.ui.adapter.holder.ViewHolderType
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.viewmodel.activity.CatalogViewModel

class CatalogActivity : BaseActivity<ActivityCatalogBinding>() {
    val viewModel: CatalogViewModel by viewModels<CatalogViewModel>()
    lateinit var webView: WebView
    lateinit var catalogAdapter: CatalogAdapter
    lateinit var bangumiAdapter: BangumiAdapter2


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
        }

        bangumiAdapter = BangumiAdapter2(this, ViewHolderType.BANGUMI_COVER_2)
        viewModel.bangumiList.observe(this) { list -> bangumiAdapter.submitList(list) }

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
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(this, HTML_OUT)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request?.url.toString().contains(Regex("\\.((css|png|jp.?g))+"))) {
                    return null
                }
                return super.shouldInterceptRequest(view, request)
            }


            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                viewModel.catalogState.value = RequestState.Error()
            }
        }
    }

    private fun initListener() {
        catalogAdapter.onTabItemSelectedListener =
            object : CatalogAdapter.OnTabItemSelectedListener {
                override fun onTabItemSelected(catalogTagPosition: Int, tabItemPosition: Int) {
                    mBinding.smartRefreshLayout.autoRefresh()
                    loadData(viewModel.getCatalogUrl(catalogTagPosition, tabItemPosition))
                }
            }


        mBinding.run {
            smartRefreshLayout.setOnLoadMoreListener {
                viewModel.getMoreBangumi()
            }

            smartRefreshLayout.setOnRefreshListener {
                loadData(viewModel.catalogUrl)
            }

            rvBangumi.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val manager = recyclerView.layoutManager as GridLayoutManager
                    val first = manager.findFirstVisibleItemPosition()
                    if (first > 10) {
                        mBinding.fabToTop.visible()
                    } else {
                        mBinding.fabToTop.gone()
                    }
                }
            })


            rvBangumi.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val manager = recyclerView.layoutManager as GridLayoutManager
                    val first = manager.findFirstVisibleItemPosition()
                    if (first > 10) {
                        mBinding.fabToTop.visible()
                    } else {
                        mBinding.fabToTop.gone()
                    }
                }
            })

            fabToTop.setOnClickListener {
//                val behavior =
//                    (mBinding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior
//                if (behavior is AppBarLayout.Behavior) {
//                    val topAndBottomOffset = behavior.topAndBottomOffset
//                    if (topAndBottomOffset != 0) {
//                        behavior.topAndBottomOffset = 0
//                    }
//                }
                mBinding.rvBangumi.scrollToPosition(0)
            }
        }

        viewModel.catalogState.observe(this) { state ->
            state.success {
                mBinding.run {
                    smartRefreshLayout.setNoMoreData(false)
                    smartRefreshLayout.finishRefresh()
                    emptyView.hide()
                }
                bangumiAdapter.submitList(it.data.bangumiList.toList())
//                val appBarChild = mBinding.appBarLayout.getChildAt(0)
//                val appBarParams = appBarChild.layoutParams
//                if (appBarParams is AppBarLayout.LayoutParams) {
//                    appBarParams.scrollFlags = if (it.data.bangumiList.isEmpty()) {
//                        AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
//                    } else {
//                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
//                    }
//                }
//                appBarChild.layoutParams = appBarParams
            }

            state.error {
                mBinding.smartRefreshLayout.finishRefresh(false)
                mBinding.emptyView.show("加载失败", "点击重试") {
                    loadData(viewModel.catalogUrl)
                }
            }
        }

        viewModel.bangumiState.observe(this) { state ->
            state.success {
                mBinding.apply {
                    smartRefreshLayout.setNoMoreData(it.data.isLastPage)
                    smartRefreshLayout.finishLoadMore(true)
                    emptyView.hide()
                }
                bangumiAdapter.submitList(it.data.bangumiList.toList())
            }

            state.error {
                mBinding.smartRefreshLayout.finishLoadMore(false)
            }
        }

    }

    private fun loadData(url: String) {
        mBinding.smartRefreshLayout.setNoMoreData(false)
        webView.loadUrl(url)
    }

    @JavascriptInterface
    fun processHTML(html: String) {
        viewModel.getCatalog(html)
    }

    companion object {
        const val HTML_OUT = "HTMLOUT"
    }
}