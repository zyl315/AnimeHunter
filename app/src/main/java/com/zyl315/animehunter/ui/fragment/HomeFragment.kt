package com.zyl315.animehunter.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zyl315.animehunter.databinding.FragmentHomeBinding
import com.zyl315.animehunter.ui.activity.CatalogActivity
import com.zyl315.animehunter.ui.activity.HistoryActivity
import com.zyl315.animehunter.ui.activity.SearchActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = super.onCreateView(inflater, container, savedInstanceState)
        mBinding.ivCatalog.setOnClickListener {

        }

        mBinding.run {
            ivCatalog.setOnClickListener {
                startActivity(Intent(requireActivity(), CatalogActivity::class.java))
            }

            ivHistory.setOnClickListener {
                startActivity(Intent(requireContext(), HistoryActivity::class.java))
            }

            tvSearch.setOnClickListener {
                startActivity(Intent(requireActivity(), SearchActivity::class.java))
            }
        }
        return view
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}