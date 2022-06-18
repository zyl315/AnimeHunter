package com.zyl315.animehunter.repository.impls.kudm

import com.zyl315.animehunter.repository.datasource.DataSourceFactory

class KudmDataSourceFactory : DataSourceFactory<KudmDataSource>() {
    override fun createDataSource(): KudmDataSource = KudmDataSource()

    companion object {
        fun create(): KudmDataSourceFactory {
            return KudmDataSourceFactory()
        }
    }

}