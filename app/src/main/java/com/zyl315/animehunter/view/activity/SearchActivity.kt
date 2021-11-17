package com.zyl315.animehunter.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.zyl315.animehunter.R
import com.zyl315.animehunter.api.SearchStatus
import com.zyl315.animehunter.databinding.ActivitySearchBinding
import com.zyl315.animehunter.util.gone
import com.zyl315.animehunter.util.invisible
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.view.adapter.SearchAdapter
import com.zyl315.animehunter.viewmodel.activity.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private var lastSearchTime: Long = System.currentTimeMillis()
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        searchAdapter = SearchAdapter(this, viewModel.mSearchResultList)
        init()
        observe()
    }

    override fun getBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.run {
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) {
                        ivSearchClear.invisible()
                    } else {
                        ivSearchClear.visible()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (v == null || v.text.toString().isBlank()) {
                            showToast(resId = R.string.input_cannot_be_empty)
                            return false
                        }

                        val currentSearchTime = System.currentTimeMillis()

                        return if (currentSearchTime - lastSearchTime > 500) {
                            lastSearchTime = currentSearchTime;
                            search(v.text.toString())
                            true
                        } else false

                    }
                    return true
                }

            })

            ivSearchClear.setOnClickListener {
                etSearch.text = null
            }

            tvSearchCancel.setOnClickListener {
                this@SearchActivity.finish()
            }

            smartRefresh.apply {
                setEnableRefresh(false)
                setOnLoadMoreListener {
                    viewModel.loadMoreData()
                }
            }

            rvSearchResult.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = searchAdapter
            }
        }
    }

    private fun observe() {
        viewModel.mSearchResultSearchStatus.observe(this, Observer {
            when (it) {
                SearchStatus.SUCCESS -> {
                    mBinding.tvSearchTip.apply {
                        visible()
                        text = viewModel.totalCount
                    }
                    mBinding.smartRefresh.visible(true)
                    searchAdapter.notifyDataSetChanged()
                }

                SearchStatus.LOAD_MORE_SUCCESS -> {
                    mBinding.smartRefresh.apply {
                        finishLoadMore(true)
                    }
                    searchAdapter.notifyDataSetChanged()
                }

                SearchStatus.LOAD_MORE_FAILED -> {
                    mBinding.smartRefresh.finishLoadMore(false)
                }
                SearchStatus.NO_MORE_DATA -> {
                    mBinding.smartRefresh.finishLoadMoreWithNoMoreData()
                }
                else -> {
                    showToast(resId = R.string.get_play_source_failed)
                }
            }
            mBinding.vsLoading.gone()
        })
    }

    fun search(keyWord: String) {
        mBinding.vsLoading.visible()
        mBinding.smartRefresh.gone()
        viewModel.getSearchData(keyWord)
    }
}