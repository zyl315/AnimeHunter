package com.zyl315.animehunter.view.activity

import android.content.Intent
import android.os.Bundle
import com.zyl315.animehunter.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            btnSearch.setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }


            btnHistory.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }

            btnCatalog.setOnClickListener {
                startActivity(Intent(this@MainActivity, CatalogActivity::class.java))
            }

        }
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
}