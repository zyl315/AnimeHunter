package com.zyl315.animehunter.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.databinding.FragmentPlaySourceBinding
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel

class PlaySourceFragment : BottomFragment<FragmentPlaySourceBinding>() {

    private lateinit var viewModel: PlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)
        val mPlaySourceAdapter =
            PlaySourceAdapter(viewModel.playSource, object : onItemClickListener {
                override fun onItemClick(position: Int) {
                    viewModel.getPlayUrl(position)
                    dismiss()
                }
            }, LinearLayoutManager.VERTICAL)

        mBinding.run {
            rvPlayUrlList.layoutManager =
                GridLayoutManager(requireActivity(), 3, RecyclerView.VERTICAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            ivClose.setOnClickListener { dismiss() }
        }

        return view
    }

    override fun onBackPress(): Boolean {
        dismiss()
        return true
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaySourceBinding = FragmentPlaySourceBinding.inflate(inflater, container, false)
}