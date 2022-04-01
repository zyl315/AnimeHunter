package com.zyl315.animehunter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.FragmentWeekPlayBinding
import com.zyl315.animehunter.ui.adapter.WeekBangumiAdapter
import com.zyl315.animehunter.viewmodel.fragment.HomeFragmentViewModel
import java.util.*

class WeekPlayFragment : BaseFragment<FragmentWeekPlayBinding>() {
    private var initialized = false
    private lateinit var weekBangumiAdapter: WeekBangumiAdapter
    val viewModel: HomeFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        weekBangumiAdapter = WeekBangumiAdapter(requireActivity())
        mBinding.rvWeekBangumi.adapter = weekBangumiAdapter
        mBinding.rvWeekBangumi.layoutManager = LinearLayoutManager(requireContext())
        initListener()
        loadData()
        return view
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeekPlayBinding {
        return FragmentWeekPlayBinding.inflate(inflater, container, false)
    }

    private fun initListener() {
        viewModel.weeklyPlayListState.observe(viewLifecycleOwner) { state ->
            state.success {
                mBinding.dduiEmptyView.hide()
                mBinding.smartRefreshLayout.finishRefresh(true)
                val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
                val list = it.data.weeklyPlayListMap[dayOfWeek]
                mBinding.tabLayout.getTabAt(dayOfWeek)?.select()
                weekBangumiAdapter.submitList(list)
            }

            state.error {
                mBinding.smartRefreshLayout.finishRefresh(false)
                if (!initialized) {
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

        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewModel.weeklyPlayListState.value?.success {
                        val list = it.data.weeklyPlayListMap[tab.position]
                        weekBangumiAdapter.submitList(list)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        mBinding.smartRefreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        viewModel.getWeekPlayList()
    }

    companion object {
        @JvmStatic
        fun newInstance() = WeekPlayFragment()
    }
}