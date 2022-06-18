package com.zyl315.animehunter.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivitySearchBinding
import com.zyl315.animehunter.repository.datasource.DataSourceManager
import com.zyl315.animehunter.ui.adapter.BangumiSearchAdapter
import com.zyl315.animehunter.util.*
import com.zyl315.animehunter.viewmodel.activity.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private var lastSearchTime: Long = System.currentTimeMillis()
    private lateinit var viewModel: SearchViewModel
    private lateinit var bangumiSearchAdapter: BangumiSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        bangumiSearchAdapter = BangumiSearchAdapter(this)
        viewModel.searchState.observe(this) { state ->
            state.success {
                it.data.apply {
                    bangumiSearchAdapter.submitList(bangumiCoverList.toList())
                    mBinding.smartRefresh.visible(currentPage == 1)
                    mBinding.tvSearchTip.visible()
                    mBinding.tvSearchTip.text = getString(R.string.total_record).format(totalCount)
                    mBinding.smartRefresh.finishLoadMore(0, true, isLastPage)
                }
            }
            state.error {
                mBinding.tvSearchTip.visible()
                mBinding.tvSearchTip.text = getString(R.string.get_data_failed)
                mBinding.smartRefresh.finishLoadMore(false)
            }
            mBinding.vsLoading.gone()
        }

        mBinding.etSearch.requestFocus()

        mBinding.ivSearchClear.setOnClickListener {
            mBinding.etSearch.text = null
        }

        mBinding.tvSearchCancel.setOnClickListener {
            this@SearchActivity.finish()
        }

        mBinding.smartRefresh.apply {
            setEnableRefresh(false)
            setOnLoadMoreListener { viewModel.loadMoreData() }
        }

        mBinding.rvSearchResult.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = bangumiSearchAdapter
        }

        mBinding.ivLeftSearchIcon.setOnClickListener {
            showBottomDataSourceDialog()
        }

        searchTextChangeListener()
        searchEditorActionListener()
    }

    override fun getBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    fun search(keyWord: String) {
        mBinding.vsLoading.visible()
        mBinding.smartRefresh.gone()
        mBinding.smartRefresh.setNoMoreData(false)
        viewModel.getSearchData(keyWord)
    }

    private fun searchTextChangeListener() {
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.isEmpty()) {
                    mBinding.ivSearchClear.invisible()
                } else {
                    mBinding.ivSearchClear.visible()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun searchEditorActionListener() {
        mBinding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView?, actionId: Int, event: KeyEvent?
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
    }

    private fun showBottomDataSourceDialog() {
        val dataSourceMap = DataSourceManager.getAllDataSource()
        val context = ContextThemeWrapper(this, R.style.QMUI_Compat_NoActionBar)
        val builder = QMUIBottomSheet.BottomListSheetBuilder(context)
            .setSkinManager(QMUISkinManager.of("Theme.AnimeHunter.QMUI", context)).setGravityCenter(true)
            .setTitle(getString(R.string.switch_search_data_sources))
            .setOnSheetItemClickListener { dialog, itemView, position, tag ->
                dataSourceMap[tag]?.let {
                    viewModel.setDataSource(it)
                    search(viewModel.searchWord)
                }
                dialog?.dismiss()
            }

        dataSourceMap.keys.forEach { dataSource ->
            builder.addItem(dataSource)
        }

        builder.build().show()
    }
}