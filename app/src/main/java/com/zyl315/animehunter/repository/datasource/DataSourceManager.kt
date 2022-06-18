package com.zyl315.animehunter.repository.datasource

import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSourceFactory
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSource
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSourceFactory

object DataSourceManager {
    enum class DataSource {
        AGEFANS, KUDM, UNKNOWN
    }

    lateinit var dataSourceFactory: DataSourceFactory<AbstractDataSource>

    fun getDataSource(dataSource: DataSource? = null): AbstractDataSource {
        return when (dataSource) {
            DataSource.AGEFANS -> AgeFansDataSourceFactory.create().createDataSource()
            DataSource.KUDM -> KudmDataSourceFactory.create().createDataSource()
            else -> dataSourceFactory.createDataSource()
        }
    }

    fun getAllDataSource(): Map<String, DataSource> {
        return mapOf("AGE" to DataSource.AGEFANS, "KUDM" to DataSource.KUDM)
    }
}