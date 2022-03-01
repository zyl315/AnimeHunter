package com.zyl315.animehunter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    private var binding: T? = null
    protected val mBinding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = getBinding(inflater, container)
        this.binding = binding
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    protected abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T
}