package com.zyl315.animehunter.repository.impls.ysjdm

import com.zyl315.animehunter.repository.datasource.AbstractDataSourceFactory

class YsjdmDataSourceFactory: AbstractDataSourceFactory<YsjdmDataSource>() {
    override fun createDataSource(): YsjdmDataSource = YsjdmDataSource()
}