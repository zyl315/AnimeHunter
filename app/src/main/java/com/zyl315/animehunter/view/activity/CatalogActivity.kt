package com.zyl315.animehunter.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.bean.BaseBean
import com.zyl315.animehunter.databinding.ActivityCatalogBinding
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.view.adapter.BangumiAdapter2
import com.zyl315.animehunter.view.adapter.CatalogAdapter
import com.zyl315.animehunter.view.adapter.CatalogShowAdapter
import com.zyl315.animehunter.view.adapter.holder.ViewHolderType
import com.zyl315.animehunter.viewmodel.activity.CatalogViewModel

class CatalogActivity : BaseActivity<ActivityCatalogBinding>() {
    val viewModel: CatalogViewModel by viewModels<CatalogViewModel>()
    lateinit var webView: WebView
    lateinit var catalogAdapter: CatalogAdapter
    lateinit var catalogShowAdapter: CatalogShowAdapter
    lateinit var bangumiAdapter: BangumiAdapter2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
        initData()
    }

    override fun getBinding() = ActivityCatalogBinding.inflate(layoutInflater)


    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
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
        }

        viewModel.catalogState.observe(this) { state ->
            state.success {
                mBinding.tvResult.text = "共${it.data.totalCount}"
                mBinding.smartRefreshLayout.setNoMoreData(false)
                bangumiAdapter.submitList(it.data.bangumiList.toList())
            }

            state.error {
                mBinding.tvResult.text = "error"
            }
        }

        viewModel.bangumiState.observe(this) { state ->
            state.success {
                bangumiAdapter.submitList(it.data.bangumiList.toList())
                mBinding.smartRefreshLayout.apply {
                    setNoMoreData(it.data.isLastPage)
                    finishLoadMore(true)
                }
            }

            state.error {
                mBinding.smartRefreshLayout.finishLoadMore(false)
            }
        }

        catalogAdapter = CatalogAdapter()
        viewModel.catalogList.observe(this) { list ->
            catalogAdapter.submitList(list.toList())
        }
        bangumiAdapter = BangumiAdapter2(this, ViewHolderType.BANGUMI_COVER_2)
        viewModel.bangumiList.observe(this) { list -> bangumiAdapter.submitList(list) }
        catalogShowAdapter = CatalogShowAdapter(this, listOf(object : BaseBean {}))

        mBinding.apply {
            rvCatalog.layoutManager =
                GridLayoutManager(this@CatalogActivity, 3).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0) {
                                3
                            } else {
                                1
                            }
                        }
                    }
                }
            rvCatalog.adapter = ConcatAdapter(catalogShowAdapter, bangumiAdapter)
            smartRefreshLayout.setEnableRefresh(false)
        }
    }

    @JavascriptInterface
    fun processHTML(html: String) {
        viewModel.getCatalog(html)
    }

    private fun initListener() {
        catalogAdapter.onTabItemSelectedListener =
            object : CatalogAdapter.OnTabItemSelectedListener {
                override fun onTabItemSelected(catalogTagPosition: Int, tabItemPosition: Int) {
                    webView.loadUrl(viewModel.getCatalogUrl(catalogTagPosition, tabItemPosition))
                }
            }
        mBinding.smartRefreshLayout.setOnLoadMoreListener {
            viewModel.getMoreBangumi()
        }

        mBinding.rvCatalog.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager as GridLayoutManager
                val first = manager.findFirstVisibleItemPosition()
                if(first > 10) {
                    mBinding.fabToTop.visible()
                } else {
                    mBinding.fabToTop.gone()
                }
            }
        })

        mBinding.fabToTop.setOnClickListener {
            mBinding.rvCatalog.scrollToPosition(0)
        }
    }

    private fun initData() {
        webView.loadUrl(viewModel.catalogUrl)
        mBinding.smartRefreshLayout.setNoMoreData(false)
    }

    companion object {
        const val HTML_OUT = "HTMLOUT"
    }
}