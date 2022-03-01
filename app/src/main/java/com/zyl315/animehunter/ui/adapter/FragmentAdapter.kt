package com.zyl315.animehunter.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter : FragmentStateAdapter {
    private var mFragmentList: List<Fragment>

    constructor(fragmentActivity: FragmentActivity, mFragmentList: List<Fragment>) : super(
        fragmentActivity
    ) {
        this.mFragmentList = mFragmentList
    }

    constructor(fragment: Fragment, mFragmentList: List<Fragment>) : super(fragment) {
        this.mFragmentList = mFragmentList
    }

    constructor(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        mFragmentList: List<Fragment>
    ) : super(fragmentManager, lifecycle) {
        this.mFragmentList = mFragmentList
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

}