package com.zyl315.animehunter.ui.fragment

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.zyl315.animehunter.R
import com.zyl315.animehunter.util.BackHandlerHelper

abstract class PopupFragment<T : ViewBinding> :
    BaseFragment<T>(),
    BackHandlerHelper.FragmentBackHandler {

    open var popupGravity: Int = Gravity.BOTTOM
    var isShow: Boolean = false

    override fun onBackPressed(): Boolean {
        return BackHandlerHelper.handleBackPress(this)
    }

    fun show(manager: FragmentManager, containerViewId: Int) {
        isShow = true
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.setPopupGravityAnimations(popupGravity)
        ft.replace(containerViewId, this)
        ft.commit()
    }

    fun dismiss() {
        isShow = false
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        ft.setPopupGravityAnimations(popupGravity)
        ft.remove(this)
        ft.commit()
    }

    @SuppressLint("RtlHardcoded")
    private fun FragmentTransaction.setPopupGravityAnimations(gravity: Int) {
        when (gravity) {
            Gravity.BOTTOM -> {
                setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
            }
            Gravity.START, Gravity.LEFT -> {
                setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
            Gravity.END, Gravity.RIGHT -> {
                setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
            }
        }
    }
}

