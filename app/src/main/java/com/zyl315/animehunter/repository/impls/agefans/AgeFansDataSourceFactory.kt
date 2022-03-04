package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.repository.datasource.DataSourceFactory

class AgeFansDataSourceFactory : DataSourceFactory<AgeFansDataSource>() {

    override fun createDataSource(): AgeFansDataSource = AgeFansDataSource()

    companion object {
        fun create(): AgeFansDataSourceFactory {
            return AgeFansDataSourceFactory()
        }
    }
}