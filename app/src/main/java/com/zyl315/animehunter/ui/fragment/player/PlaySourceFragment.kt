package com.zyl315.animehunter.ui.fragment.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zyl315.animehunter.databinding.FragmentPlaySourceBinding
import com.zyl315.animehunter.ui.adapter.PlaySourceAdapter
import com.zyl315.animehunter.ui.adapter.interfaces.OnItemClickListener
import com.zyl315.animehunter.ui.fragment.PopupFragment
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel


class PlaySourceFragment(override var popupGravity: Int) : PopupFragment<FragmentPlaySourceBinding>() {

    var backgroundColorId: Int? = null
    var showCloseIcon: Boolean = true
    lateinit var viewModel: PlayViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        backgroundColorId?.let { view?.setBackgroundResource(it) }
        viewModel = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)

        val currentPosition = if (viewModel.isCurrentPlaySource()) viewModel.playEpisodeIndex else -1

        val mPlaySourceAdapter = PlaySourceAdapter(
            LinearLayoutManager.VERTICAL, currentPosition, viewModel.getEpisodeList(viewModel.selectSourceIndex)
        )
        mPlaySourceAdapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.playSourceIndex = viewModel.selectSourceIndex
                viewModel.getPlayUrl(position)
                dismiss()
            }
        }

        mBinding.run {
            rvPlayUrlList.layoutManager = GridLayoutManager(requireActivity(), 3)
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
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaySourceBinding = FragmentPlaySourceBinding.inflate(inflater, container, false)
}