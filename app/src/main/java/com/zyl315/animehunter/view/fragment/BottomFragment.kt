package com.zyl315.animehunter.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.zyl315.animehunter.R
import com.zyl315.animehunter.util.BackHandlerHelper

abstract class BottomFragment<T : ViewBinding> : Fragment(), BackHandlerHelper.FragmentBackHandler {
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

    override fun onBackPress(): Boolean {
        return BackHandlerHelper.handleBackPress(this)
    }

    fun show(manager: FragmentManager, containerViewId: Int) {
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        ft.add(containerViewId, this)
        ft.commit()
    }

    fun dismiss() {
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        ft.remove(this)
        ft.commit()
    }

    protected abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T
}

