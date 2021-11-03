package com.zyl315.animehunter.view.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivitySearchBinding
import com.zyl315.animehunter.util.invisible
import com.zyl315.animehunter.util.showToast
import com.zyl315.animehunter.util.visible
import com.zyl315.animehunter.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private var lastSearchTime: Long = System.currentTimeMillis()
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        init()
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
                            showToast(message = R.string.input_cannot_be_empty)
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
        }
    }

    fun search(keyWord: String) {
        viewModel.getSearchData(keyWord, 1)
    }
}