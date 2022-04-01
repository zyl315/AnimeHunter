package com.zyl315.animehunter.ui.fragment

import DarkModeUtils
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.FragmentHomeBinding
import com.zyl315.animehunter.ui.activity.CatalogActivity
import com.zyl315.animehunter.ui.activity.HistoryActivity
import com.zyl315.animehunter.ui.activity.SearchActivity
import com.zyl315.animehunter.ui.adapter.HomeContentAdapter
import com.zyl315.animehunter.viewmodel.fragment.HomeFragmentViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var homeContentAdapter: HomeContentAdapter
    private var isInitialized = false

    val viewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initView()
        initListener()
        loadData()
        return view
    }

    private fun initView() {
        homeContentAdapter = HomeContentAdapter(requireActivity())

        mBinding.run {
            rvHomepage.layoutManager = LinearLayoutManager(requireActivity())
            rvHomepage.adapter = homeContentAdapter

            smartRefreshLayout.setOnRefreshListener {
                loadData()
            }

            ivNightModelSwitch.setImageResource(
                if (DarkModeUtils.isDarkMode(requireContext())) {
                    R.drawable.ic_night
                } else {
                    R.drawable.ic_day
                }
            )
        }
    }

    private fun initListener() {
        mBinding.run {
            ivCatalog.setOnClickListener {
                startActivity(Intent(requireActivity(), CatalogActivity::class.java))
            }

            ivHistory.setOnClickListener {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
            }

            ivNightModelSwitch.setOnClickListener {
                if (DarkModeUtils.isDarkMode(requireContext())) {
                    DarkModeUtils.applyDayMode(requireContext())
                    ivNightModelSwitch.setImageResource(R.drawable.ic_day)
                } else {
                    DarkModeUtils.applyNightMode(requireContext())
                    ivNightModelSwitch.setImageResource(R.drawable.ic_night)
                }
            }

            tvSearch.setOnClickListener {
                startActivity(Intent(requireActivity(), SearchActivity::class.java))
            }
        }

        viewModel.homeContentState.observe(viewLifecycleOwner) { state ->
            state.success {
                homeContentAdapter.submitList(it.data.contentList)
                mBinding.smartRefreshLayout.finishRefresh(true)
                if (isInitialized) {
                    mBinding.smartRefreshLayout.setEnableRefresh(true)
                } else {
                    mBinding.smartRefreshLayout.finishRefresh(true)
                }
                mBinding.dduiEmptyView.hide()
            }

            state.error {
                mBinding.smartRefreshLayout.finishRefresh(false)
                if (!isInitialized) {
                    mBinding.dduiEmptyView.show(
                        getString(R.string.loaded_failed),
                        getString(R.string.retry_click)
                    ) {
                        mBinding.dduiEmptyView.show(true)
                        loadData()
                    }
                }
            }
        }
    }

    private fun loadData() {
        viewModel.getHomeContent()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}