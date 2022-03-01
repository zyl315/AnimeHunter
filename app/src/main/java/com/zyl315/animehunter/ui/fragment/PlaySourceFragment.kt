package com.zyl315.animehunter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.databinding.FragmentPlaySourceBinding
import com.zyl315.animehunter.ui.adapter.PlaySourceAdapter
import com.zyl315.animehunter.ui.adapter.onItemClickListener
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel

class PlaySourceFragment(override var popupGravity: Int) :
    PopupFragment<FragmentPlaySourceBinding>() {

    var backgroundColorId: Int? = null
    var showCloseIcon: Boolean = true
    private lateinit var viewModel: PlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)?.apply {
            backgroundColorId?.let { setBackgroundResource(it) }
        }

        viewModel = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)

        val currentPosition =
            if (viewModel.isCurrentPlaySource()) viewModel.playEpisodeIndex else -1

        val mPlaySourceAdapter =
            PlaySourceAdapter(
                viewModel.getEpisodeList(viewModel.playSourceIndex),
                LinearLayoutManager.VERTICAL,
                currentPosition,
                object : onItemClickListener {
                    override fun onItemClick(position: Int) {
                        viewModel.getPlayUrl(position)
                        dismiss()
                    }
                },
            )

        mBinding.run {
            rvPlayUrlList.layoutManager =
                GridLayoutManager(requireActivity(), 3, RecyclerView.VERTICAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            ivClose.visibility = if (showCloseIcon) View.VISIBLE else View.GONE
            ivClose.setOnClickListener { dismiss() }
        }
        return view
    }

    override fun onBackPressed(): Boolean {
        dismiss()
        return true
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaySourceBinding = FragmentPlaySourceBinding.inflate(inflater, container, false)
}