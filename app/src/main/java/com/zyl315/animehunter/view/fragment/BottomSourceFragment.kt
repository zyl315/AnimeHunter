package com.zyl315.animehunter.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyl315.animehunter.databinding.FragmentBottomSourceBinding
import com.zyl315.animehunter.util.dp
import com.zyl315.animehunter.view.adapter.PlaySourceAdapter
import com.zyl315.animehunter.view.adapter.onItemClickListener
import com.zyl315.animehunter.viewmodel.activity.PlayViewModel

class BottomSourceFragment(private val height: Int) : BottomDialogFragment<FragmentBottomSourceBinding>() {

    private lateinit var viewModel: PlayViewModel


    override fun onStart() {
        super.onStart()
        if (height > 0) {
            dialog?.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
                setBackgroundDrawable(null)
            }
        }
    }

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
                    this@BottomSourceFragment.dismiss()
                }
            }, LinearLayoutManager.VERTICAL)

        mBinding.run {
            rvPlayUrlList.layoutManager =
                GridLayoutManager(requireActivity(), 3, RecyclerView.VERTICAL, false)
            rvPlayUrlList.adapter = mPlaySourceAdapter
            ivClose.setOnClickListener { this@BottomSourceFragment.dismiss() }

            if (height > 0) {
                content.layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
            }
        }
        return view
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBottomSourceBinding {
        return FragmentBottomSourceBinding.inflate(inflater, container, false)
    }


}