package com.zyl315.animehunter.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.SearchStatus
import com.zyl315.animehunter.databinding.ActivityCatalogBinding
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.view.adapter.BangumiAdapter
import com.zyl315.animehunter.view.adapter.CatalogAdapter
import com.zyl315.animehunter.viewmodel.activity.CatalogViewModel

class CatalogActivity : BaseActivity<ActivityCatalogBinding>() {
    lateinit var webView: WebView
    lateinit var viewModel: CatalogViewModel
    lateinit var catalogAdapter: CatalogAdapter
    lateinit var bangumiAdapter: BangumiAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CatalogViewModel::class.java)

        observe()
        initView()
        initListener()
        initData()
    }

    override fun getBinding() = ActivityCatalogBinding.inflate(layoutInflater)

    private fun observe() {
        viewModel.getCatalogSuccess.observe(this) { success ->
            if (success) {
                mBinding.apply {
                    tvResult.text = "共${viewModel.totalCount}"
                }
            }
        }

        viewModel.searchResultStatus.observe(this) { searchStatus ->
            when (searchStatus) {
                SearchStatus.SUCCESS -> {
                    mBinding.smartRefreshLayout.setNoMoreData(false)
                    mBinding.tvResult.text = "共${viewModel.totalCount}"
                }
                SearchStatus.FAILED -> {
                    showToast(resId = R.string.get_data_failed)
                }

                SearchStatus.LOAD_MORE_SUCCESS -> {
                    mBinding.tvResult.text = "共${viewModel.totalCount}"
                    mBinding.smartRefreshLayout.finishLoadMore(true)
                }

                SearchStatus.LOAD_MORE_FAILED -> {
                    mBinding.smartRefreshLayout.finishLoadMore(false)
                }

                SearchStatus.NO_MORE_DATA -> {
                    mBinding.smartRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            }
        }
    }


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

        catalogAdapter = CatalogAdapter()
        viewModel.catalogList.observe(this) { list -> catalogAdapter.submitList(list) }
        bangumiAdapter = BangumiAdapter(this)
        viewModel.bangumiList.observe(this) { list -> bangumiAdapter.submitList(list) }


        mBinding.apply {
            rvCatalog.layoutManager = LinearLayoutManager(this@CatalogActivity)
            rvCatalog.adapter = ConcatAdapter(catalogAdapter, bangumiAdapter)
            smartRefreshLayout.setEnableRefresh(false)
        }
    }

    @JavascriptInterface
    fun processHTML(html: String) {
        viewModel.getAllData(html)
    }

    private fun initListener() {
        catalogAdapter.onTabItemSelectedListener =
            object : CatalogAdapter.OnTabItemSelectedListener {
                override fun onTabItemSelected(catalogTagPosition: Int, tabItemPosition: Int) {
                    webView.loadUrl(viewModel.getCatalogUrl(catalogTagPosition, tabItemPosition))
                }
            }
        mBinding.smartRefreshLayout.setOnLoadMoreListener {
            viewModel.loadMoreBangumi()
        }
    }

    private fun initData() {
        webView.loadUrl(viewModel.catalogUrl)
    }

    companion object {
        const val HTML_OUT = "HTMLOUT"
    }
}