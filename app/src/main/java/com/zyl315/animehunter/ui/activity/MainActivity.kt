package com.zyl315.animehunter.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.zyl315.animehunter.R
import com.zyl315.animehunter.databinding.ActivityMainBinding
import com.zyl315.animehunter.ui.adapter.FragmentAdapter
import com.zyl315.animehunter.ui.fragment.HomeFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mFragmentAdapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentList = listOf<Fragment>(
            HomeFragment.newInstance(), HomeFragment.newInstance(), HomeFragment.newInstance()
        )
        mFragmentAdapter = FragmentAdapter(this, fragmentList)


        mBinding.run {
            viewPager2.adapter = mFragmentAdapter

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bnvNav.menu.getItem(position).isChecked = true
                }
            })

            bnvNav.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_item_home -> viewPager2.currentItem = 0
                    R.id.menu_item_favor -> viewPager2.currentItem = 1
                    R.id.menu_item_mine -> viewPager2.currentItem = 2
                }
                true
            }
        }
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
}