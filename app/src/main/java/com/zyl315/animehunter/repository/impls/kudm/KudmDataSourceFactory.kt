package com.zyl315.animehunter.repository.impls.kudm

import com.zyl315.animehunter.repository.datasource.AbstractDataSourceFactory

class KudmDataSourceFactory : AbstractDataSourceFactory<KudmDataSource>() {
    override fun createDataSource(): KudmDataSource = KudmDataSource()
}